/**
 * The HomePage is the history page for the login user (presenter), where the user can open the previous presentation,
 * download raw Markdown file and static HTML file, delete the presentation from the database and control the sharing permission.
 */

import React, {useContext, useEffect, useLayoutEffect, useState} from 'react';
import {Layer, Stage, Text} from 'react-konva';
import SlideBackground from "./SlideBackground";
import SlideText from "./SlideText";
import SlideButton from "./SlideButton";
import DeleteButton from "./DeleteButton";
import ViewButton from "./ViewButton";
import 'antd/dist/antd.css';
import '../../App.css';
import {Link} from "react-router-dom";
import {CopyToClipboard} from "react-copy-to-clipboard";
import {List, Button, Menu, message} from 'antd';
import {DeleteOutlined, EditOutlined, FilePptOutlined, DownloadOutlined, ShareAltOutlined, StopOutlined, FilePdfOutlined} from "@ant-design/icons";
import axios from "axios";
import {BASE_URL} from "../../config/config";
import separateQuestion from "../Parse";
import marpitConvert from "../Marpit";
import fileListContext from "./fileListContext";
import marpit2spectacle from "../default_theme/marpit2spectacle";
import styled from "styled-components";


export default function History(props) {

    const { fileList } = useContext(fileListContext);
    const [layout, setLayout] = useState("list");
    /**
     * useEffect with deps: [] is a Hook in function similar to componentDidMount() in class,
     * which is the function mounted whenever this page is loaded (refreshed) for only one time.
     * request all the history files by sending the userId to back end.
     */
    useEffect(() => props.refreshCallback(), []);


    const fetchFile = (fileId) => {
        return new Promise((resolve, reject) => {
            let params = {
                fileId: fileId
            }
            axios.get(BASE_URL + "/fetch",  {params})
                .then(res => {
                    message.success(`File ${fileId} fetched successfully.`);
                    resolve(res.data);
                })
                .catch((error) => {
                    alert(`Fail to fetch File ${fileId}. ${error}`);
                    reject(error);
                })
        })
    }

    const readCSS = (fileId) => {
        return new Promise(((resolve, reject) => {
            let params = {
                fileId: fileId
            }
            axios.get(BASE_URL + "/readCSS", {params})
                .then(res => {
                    // localStorage.setItem("CSS", res.data);
                    resolve(res.data);
                })
                // no user specified CSS, will catch error
                .catch((e) => {
                    console.log(`error: ${e}`);
                    reject(e);
                })
        }))
    }

    const editFile = (fileId, fileName) => {
        if (localStorage.getItem("saved") === "true" || !localStorage.hasOwnProperty('saved')) {
            localStorage.setItem("saved", "true");
            localStorage.setItem("fileId", fileId);
            localStorage.setItem("newFileName", fileName);
            fetchFile(fileId)
                .then(rawString => {
                    localStorage.setItem("newFileString", rawString);
                    window.open("/EditPage", "_self");
                });
        } else {
            alert("There are unsaved changes, please save them first!");
            window.open("/EditPage", "_self");
        }

    }

    /**
     * presenterMode(fileId) is the function used to open the presenter mode from the history page.
     * @param fileId
     * presenterMode(fileId) fetch the file with fileId and pass the rawSting to callSeparateQuestion(rawString, fileId).
     */
    const presenterMode = (fileId) => {
        fetchFile(fileId)
            .then(rawString => {
                readCSS(fileId)
                    .then(CSS => callSeparateQuestion(rawString, fileId, CSS))
            });
    }

    /**
     * callSeparateQuestion(rawString, fileId) is a helper function to call separateQuestion from Parse.js
     * separateQuestion(rawString) will parse the raw string to a JSON parameter which contains quizzes and slides.
     * data = {
     *     fileId : fileId,
     *     quiz : [],
     *     slidesString : []
     * }
     * which will be set to localStorage in browser, which will be used in PresenterPage.js
     * @param rawString
     * @param fileId
     * @param CSS
     */
    const callSeparateQuestion = (rawString, fileId, CSS) =>{
        var data = separateQuestion(rawString);
        data.fileId = fileId;
        console.log(CSS);
        data.CSS = marpit2spectacle(CSS);
        data = JSON.stringify(data);
        localStorage.setItem("data", data)
        window.open('/presenter');
    }

    /**
     * startSharing(fileId) is a function for presenter to open the sharing permission.
     * @param fileId
     */
    const startSharing = (fileId) => {
        const formData = new FormData();
        formData.append('fileId', fileId);
        formData.append('permission', true);
        axios.post(BASE_URL + "/filepermission", formData)
            .then(()=> message.success(`Share code ${fileId} is copied on your clipboard`))
            .catch((error)=> message.error(error));
    }

    /**
     * stopSharing(fileId) is a function for presenter to stop sharing.
     * @param fileId
     */
    const stopSharing = (fileId) => {
        const formData = new FormData();
        formData.append('fileId', fileId);
        formData.append('permission', false);
        axios.post(BASE_URL + "/filepermission", formData)
            .then(()=> message.success(`File ${fileId} stop sharing`))
            .catch((error)=> message.error(error));
    }

    /**
     * onDownload(fileId, fileName, fileType) is used to handle download request, both raw Markdown file and static HTML file.
     * It is decided by the last parameter fileType.
     * @param fileId
     * @param fileName
     * @param fileType
     */
    const onDownload = (fileId, fileName, fileType) => {
        function fakeClick(obj) {
            var ev = document.createEvent("MouseEvents");
            ev.initMouseEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            obj.dispatchEvent(ev);
        }
        function exportRaw(name, data) {
            var urlObject = window.URL || window.webkitURL || window;
            var export_blob = new Blob([data]);
            var save_link = document.createElementNS("http://www.w3.org/1999/xhtml", "a")
            save_link.href = urlObject.createObjectURL(export_blob);
            save_link.download = name;
            fakeClick(save_link);
        }

        fetchFile(fileId)
            .then(rawString => {
                if (fileType === "raw") exportRaw(fileName, rawString);
                else if (fileType === "HTML") {
                    readCSS(fileId)
                        // has user specified CSS
                        .then(CSS => exportRaw(`${fileName}.html`, marpitConvert(rawString, CSS)))
                        // No user specified CSS
                        .catch(e => exportRaw(`${fileName}.html`, marpitConvert(rawString)))
                }
            })
    }

    /**
     * delete(fileId) is used to delete the corresponding file from the database.
     * Note: componentDidMount() is called to refresh the page.
     * @param fileId
     */
    const deleteFile = (fileId) => {
        const formData = new FormData();
        formData.append('fileId', fileId);
        axios.post(BASE_URL + "/deletefile", formData)
            .then(() => {
                props.refreshCallback();
                message.success(`File ${fileId} deleted successfully`)})
            .catch((error => {
                props.refreshCallback();
                alert(`Fail to delete File ${fileId}. ${error}`)
            }))
    }

    /**
     * return rendered HomePage page. Use <List.Item/> to show the list of uploaded files.
     */
    // const { fileList } = this.state;

    return (
        <div>
            <div style={{display: "flex"}}>
                <Button style={{ marginLeft: "auto" }}
                    onClick={() => {
                        if (layout === "grid") setLayout("list");
                        else setLayout("grid");
                    }}>
                    Change layout
                </Button>
            </div>

            {/* Tab form */}
            <List margin-top={"50px"}
                  className="demo-loadmore-list"
                  itemLayout="horizontal"
                  style={{display: layout === "list" ? "block" : "none"}}
                  dataSource={fileList}
                  renderItem={item => (
                      <List.Item
                          actions={[
                              <Button size={'small'}
                                      onClick={() => deleteFile(item.fileId)}>
                                  <DeleteOutlined /> Delete
                              </Button>,
                              // <Link to={{pathname: '/EditPage'}}>
                              //     <Button size={'small'}
                              //             onClick={() => editFile(item.fileId, item.fileName)}>
                              //         <EditOutlined /> Edit
                              //     </Button>
                              // </Link>,
                              // <Link to={{pathname: '/presenter'}} target = '_blank'>
                              <Button size={"small"}
                                      onClick={() => presenterMode(item.fileId)}>
                                  <FilePptOutlined /> Presentation
                              </Button>,
                              // </Link>,
                              /**
                               *  Do not delete comment, This is another way to write the function of jump to a new tab {pathname: '/presenter'}
                               */

                              // Start/Stop sharing file button
                              <Button size={"small"}
                                      onClick={() => onDownload(item.fileId, item.fileName, "raw")}>
                                  <DownloadOutlined /> Download Raw
                              </Button>,
                              <Button size={"small"}
                                      onClick={() => onDownload(item.fileId, item.fileName, "HTML")}>
                                  <FilePdfOutlined /> Download HTML
                              </Button>,
                              <CopyToClipboard text={item.fileId}
                                               onCopy={() => startSharing(item.fileId)}>
                                  <Button size={"small"}>
                                      <ShareAltOutlined /> Start sharing
                                  </Button>
                              </CopyToClipboard>,
                              <Button size={"small"}
                                      onClick={() => stopSharing(item.fileId)}>
                                  <StopOutlined /> Stop sharing
                              </Button>
                          ]}
                      >
                          <List.Item.Meta style={{float:"left", marginLeft:"0px", width: "0px"}}
                                          title={<a onClick={() => editFile(item.fileId, item.fileName)}>
                                                    <EditOutlined />  {item.fileName}
                                                </a>}
                          />
                      </List.Item>
                  )}
            />
            {/*Slide Form*/}
            <List
                grid={{ gutter: 25, column: window.innerWidth * 0.0034 }}
                style={{display: layout === "grid" ? "block" : "none"}}
                dataSource={fileList}
                renderItem={item => (
                    <List.Item>
                        <Stage width={240} height={140}>
                            <Layer>
                                <SlideBackground x={10} y={10} width={200} height={112}/>
                                <SlideText title={item.fileName}
                                           x={10} y={10}
                                           width={200} height={112}
                                           offsetY={-50}
                                           edit={() => editFile(item.fileId, item.fileName)}
                                />
                                <DeleteButton x={10} y={10}
                                              width={25} height={25}
                                              delete={() => deleteFile(item.fileId)}
                                />
                                <ViewButton x={12} y={95}
                                              width={25} height={25}
                                              present={() => presenterMode(item.fileId)}
                                />
                                <SlideButton x={120} y={10} width={90}
                                             // delete={() => deleteFile(item.fileId)}
                                             downloadRaw={() => onDownload(item.fileId, item.fileName, "raw")}
                                             // present={() => presenterMode(item.fileId)}
                                             downloadHTML={() => onDownload(item.fileId, item.fileName, "HTML")}
                                             share={() => startSharing(item.fileId)}
                                             stopShare={() => stopSharing(item.fileId)}
                                />
                            </Layer>
                        </Stage>
                    </List.Item>
                )}
            />

        </div>

    );
}