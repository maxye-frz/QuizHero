/**
 * UploadPage renders the page where the presenter can upload his/her markdown file.
 */

import React, { useState } from "react";
import {Link} from "react-router-dom"
import {Button, Icon, message, Upload} from 'antd';
import {PlusCircleOutlined} from "@ant-design/icons";
import axios from 'axios';
import {BASE_URL} from "../../config/config"
import styled from "styled-components";
import separateQuestion from "../Parse";
import jwt_decode from "jwt-decode";
import cookie from "react-cookies";
import "../../style/HomePageStyle.css"

const Header = styled.div`
  background-color: #ffffff !important;
  // min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: calc(10px + 2vmin);
  color: black;
`

export default function UploadNew (props){

    const uploadprops = {
        name: 'file',
        action: 'https://www.mocky.io/v2/5cc8019d300000980a055e76',
        headers: {
            authorization: 'authorization-text',
        },
    };

    const [ fileName, setFileName ] = useState("fileName");
    const [ rawString, setRawString ] = useState("rawString");

    const loginInfo = jwt_decode(cookie.load('token'));

    /**
     * Catch the uploaded file and handle multiple uploads error.
     * @param file
     * @returns {boolean}
     */
    const beforeUpload = (file) => {
        // if (state.file === ""){
        //     console.log("FILEEE",file);
        //     state.file = file;
        // }else{
        //     alert("You can only upload one file at a time. Please delete the previous uploaded file.");
        //     return false;
        // }
    }

    /**
     * Handle different upload status.
     * Mainly used to initiate 1. sendFile 2. ReadFile 3. callSeparateQuestion if upload status is "done".
     * @param info
     */
    const onChange = (info) => {
        if (info.file.status !== 'uploading') {
            console.log(info.file, info.fileList);
            // Delete redundant file in fileList when user tries to upload a second file without deleting the first one
            // if (info.fileList.length > 1){
            //     info.fileList.pop();
            // }
            // console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
            console.log(info.file.name);
            setFileName(info.file.name);
            console.log(fileName);
            console.log(info.file.originFileObj);
            // readFile(info.file.originFileObj).then((r) => {
            //     setRawString(r);
            //     console.log(rawString);
            // });
            // console.log(rawString);
            message.success(`${info.file.name} file uploaded successfully`);
            // Send uploaded

            readFile(info.file.originFileObj)
                .then(r => {
                    saveFile(info.file.name, r)
                        .then(fileId => {
                            setRawString(r);
                            separateQuestion(r, fileId);
                            props.refreshCallback();
                        });
                });

        } else if (info.file.status === 'error') {
            console.log(info.file.name);
            message.error(`${info.file.name} file upload failed.`);
        }
    }

    /**
     * Remove the previously uploaded file to upload a new file.
     */
    const onRemove = () => {

    }

    /**
     * onDownload(fileType) is used to handle download request, both raw Markdown file and static HTML file.
     * It is decided by fileType.
     * @param fileType
     */
    const onDownload = (fileType) => {
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

        console.log(fileName);
        console.log(rawString);
        if (fileType === "raw") exportRaw(fileName, rawString);
        // else if (fileType === "HTML") exportRaw(`${fileName}.html`, marpitResult);
        else console.log("Wrong fileType provided")
    }

    /**
     * send markdown file to backend and set the database returned fileId to state
     */
    const saveFile = (fileName, raw) => {
        return new Promise((resolve, reject) => {
            const formData = new FormData();
            formData.append('fileId', "");
            formData.append('fileName', fileName);
            formData.append('rawString', raw);
            formData.append('userId', loginInfo['userId']);
            formData.append('repoId', loginInfo['repoId']);

            console.log("Send data to backend", formData);
            axios.post(BASE_URL + "/save", formData)
                .then(res => {
                    console.log("CCC", res.data);
                    resolve(res.data.fileId);
                    // alert("File uploaded successfully.");
                })
                .catch((error) => {
                    reject(error);
                });
        });
    }

    /**
     * This is a function that read the uploaded file into a string.
     * @returns {Promise<unknown>}
     */
    const readFile = (file) => {
        const reader = new FileReader();
        return new Promise((resolve, reject) => {
            reader.onload = (e) => {
                // console.log(e.target.result);
                // console.log(typeof reader.result);
                setRawString(reader.result);
                console.log(rawString);
                resolve(reader.result);
            };
            reader.onerror = function (e) {
                reject(e);
            };
            reader.readAsText(file);
        });
    }


    return(
        <Header>

            {/*<img src={logo} className="App-logo" alt="logo"/>*/}

            {/* Upload button*/}
            <div>
                <Upload
                    onChange={onChange}
                    beforeUpload={beforeUpload}
                    onDownload={() => onDownload("raw")}
                    onPreview={() => onDownload("raw")}
                    onRemove={onRemove}
                    {...uploadprops}>

                    <Button className="upload-button">
                        <Icon type = 'upload' /> Upload from local
                    </Button>

                </Upload>
            </div>

            <Link to={{pathname: '/EditPage'}}>
                <Button
                    className="new-button"
                    onClick={() => {
                        if (localStorage.getItem("saved") === "true" || !localStorage.hasOwnProperty('saved')) {
                            localStorage.setItem("saved", "true");
                            localStorage.setItem("fileId", "");
                            localStorage.setItem("newFileName", "");
                            localStorage.setItem("newFileString", "");
                        } else {
                            alert("There are unsaved changes, please save or discard them first!");
                        }}}>
                    <PlusCircleOutlined /> New File
                </Button>
            </Link>

        </Header>
    );
}