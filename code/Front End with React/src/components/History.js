/**
 * The HomePage is the history page for the login user (presenter), where the user can open the previous presentation,
 * download raw Markdown file and static HTML file, delete the presentation from the database and control the sharing permission.
 */

import React from 'react';
import 'antd/dist/antd.css';
import '../App.css'
import {Link} from "react-router-dom";
import {CopyToClipboard} from "react-copy-to-clipboard";
import {List, Button, Menu, message} from 'antd';
import {DeleteOutlined, EditOutlined, FilePptOutlined, DownloadOutlined, ShareAltOutlined, StopOutlined} from "@ant-design/icons";
import axios from "axios";
import {BASE_URL} from "../config/config";
import separateQuestion from "../components/Parse";
import marpitConvert from "../components/Marpit";
import UploadButton from "../components/Upload";
import styled from "styled-components";

class HomePage extends React.Component {
    state = {
        fileList: [],
        data : "",
        fileId : ""
    };

    /**
     * componentDidMount() is the function mounted whenever this page is loaded (refreshed).
     * componentDidMount() request all the history files by sending the instructorId to back end.
     */
    componentDidMount() {
        this.refreshCallback();
    }

    refreshCallback = () => {
        let params = {
            instructorId : localStorage.getItem("instructorId")
        }
        console.log(params)
        axios
            .get(BASE_URL + "/history", {params})
            .then((res) => {
                if(res.status === 200){
                    console.log("res",res);
                    console.log(this.state.fileList);
                    this.setState({fileList : res.data});
                    console.log(this.state.fileList);
                }
            })
            .catch((error) => {
                console.log(error)
            });
    }

    fetchFile = (fileId) => {
        return new Promise(((resolve, reject) => {
            let params = {
                fileId: fileId
            }
            console.log(fileId)
            axios.get(BASE_URL + "/fetch",  {params})
                .then(res => {
                    console.log("AAA", res.data);
                    message.success(`File ${fileId} fetched successfully.`);
                    resolve(res.data);
                })
                .catch((error) => {
                    alert(`Fail to fetch File ${fileId}. ${error}`);
                    reject(error);
                })
        }))
    }

    editFile = (fileId, fileName) => {
        if (localStorage.getItem("saved") === "true" || !localStorage.hasOwnProperty('saved')) {
            localStorage.setItem("saved", "true");
            localStorage.setItem("fileId", fileId);
            localStorage.setItem("newFileName", fileName);
            this.fetchFile(fileId)
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
    presenterMode = (fileId) => {
        // let params = {
        //     fileId: fileId
        // }
        // console.log(fileId)
        // axios.get(BASE_URL + "/fetch",  {params})
        //     .then(res => {
        //         console.log("AAA", res.data);
        //         this.callSeparateQuestion(res.data, fileId);
        //         message.success(`File ${fileId} fetched successfully.`);
        //     })
        //     .catch((error) => {
        //         alert(`Fail to fetch File ${fileId}. ${error}`)
        //     })
        this.fetchFile(fileId)
            .then(rawString => {this.callSeparateQuestion(rawString, fileId)});
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
     */
    callSeparateQuestion = (rawString, fileId) =>{
        var data = separateQuestion(rawString);
        data.fileId = fileId;
        data = JSON.stringify(data);
        localStorage.setItem("data", data)
        window.open('/presenter');
    }

    /**
     * startSharing(fileId) is a function for presenter to open the sharing permission.
     * @param fileId
     */
    startSharing = (fileId) => {
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
    stopSharing = (fileId) => {
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
    onDownload = (fileId, fileName, fileType) => {
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

        // let params = {
        //     fileId: fileId
        // }
        // console.log(fileId)
        // axios.get(BASE_URL + "/fetch", {params})
        //     .then(res => {
        //         console.log("AAA", res.data);
        //         if (fileType === "raw") exportRaw(fileName, res.data);
        //         else if (fileType === "HTML") exportRaw(`${fileName}.html`, marpitConvert(res.data));
        //         message.success(`File ${fileName} downloaded successfully.`)
        //     })
        //     .catch((error) => {
        //         console.log(error);
        //         alert(`Fail to fetch File ${fileId}. ${error}`)
        //     })
        this.fetchFile(fileId)
            .then(rawString => {
                if (fileType === "raw") exportRaw(fileName, rawString);
                else if (fileType === "HTML") exportRaw(`${fileName}.html`, marpitConvert(rawString));
            })
    }

    /**
     * delete(fileId) is used to delete the corresponding file from the database.
     * Note: componentDidMount() is called to refresh the page.
     * @param fileId
     */
    deleteFile = (fileId) => {
        const formData = new FormData();
        formData.append('fileId', fileId);
        axios.post(BASE_URL + "/deletefile", formData)
            .then(() => {
                this.componentDidMount();
                message.success(`File ${fileId} deleted successfully`)})
            .catch((error => {
                this.componentDidMount();
                alert(`Fail to delete File ${fileId}. ${error}`)
            }))
    }


    /**
     * return rendered HomePage page. Use <List.Item/> to show the list of uploaded files.
     */
    render() {
        const { fileList } = this.state;

        return (
            <List margin-top={"50px"}
                  className="demo-loadmore-list"
                  itemLayout="horizontal"
                  dataSource={fileList}
                  renderItem={item => (
                      <List.Item
                          actions={[
                              <Button size={'small'}
                                      onClick={() => this.deleteFile(item.fileId)}>
                                  <DeleteOutlined /> Delete
                              </Button>,
                              <Link to={{pathname: '/EditPage'}}>
                                  <Button size={'small'}
                                          onClick={() => this.editFile(item.fileId, item.fileName)}>
                                      <EditOutlined /> Edit
                                  </Button>
                              </Link>,
                              // <Link to={{pathname: '/presenter'}} target = '_blank'>
                              <Button size={"small"}
                                      onClick={() => this.presenterMode(item.fileId)}>
                                  <FilePptOutlined /> Presentation
                              </Button>,
                              // </Link>,
                              /**
                               *  Do not delete comment, This is another way to write the function of jump to a new tab {pathname: '/presenter'}
                               */

                              // Start/Stop sharing file button
                              <Button size={"small"}
                                      onClick={() => this.onDownload(item.fileId, item.fileName, "HTML")}>
                                  <DownloadOutlined /> Download HTML
                              </Button>,
                              <CopyToClipboard text={item.fileId}
                                               onCopy={() => this.startSharing(item.fileId)}>
                                  <Button size={"small"}>
                                      <ShareAltOutlined /> Start sharing
                                  </Button>
                              </CopyToClipboard>,
                              <Button size={"small"}
                                      onClick={() => this.stopSharing(item.fileId)}>
                                  <StopOutlined /> Stop sharing
                              </Button>
                          ]}
                      >
                          <List.Item.Meta style={{float:"left", marginLeft:"0px", width: "0px"}}
                                          title={<a onClick={() => this.onDownload(item.fileId, item.fileName, "raw")}>{item.fileName}</a>}
                          />
                      </List.Item>
                  )}
            />
        );
    }
}

export default HomePage;