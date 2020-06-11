/**
 * UploadPage renders the page where the presenter can upload his/her markdown file.
 */

import React from "react";
import {Link} from "react-router-dom"
import {Upload, message, Button, Icon} from 'antd';
import {PlusOutlined, PlusCircleOutlined, PlusSquareOutlined} from "@ant-design/icons";
import axios from 'axios';
import {BASE_URL} from "../config/config"
import styled from "styled-components";

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

export default function UploadButton (props){

    const uploadprops = {
        name: 'file',
        action: 'https://www.mocky.io/v2/5cc8019d300000980a055e76',
        headers: {
            authorization: 'authorization-text',
        },
    };

    const state = {
        file:"",
        fileId:"",
        fileName:"",
        rawString:"",
    }

    /**
     * Catch the uploaded file and handle multiple uploads error.
     * @param file
     * @returns {boolean}
     */
    const beforeUpload = (file) => {
        if (state.file === ""){
            console.log("FILEEE",file);
            state.file = file;
        }else{
            alert("You can only upload one file at a time. Please delete the previous uploaded file.");
            return false;
        }
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
            if (info.fileList.length > 1){
                info.fileList.pop();
            }
            console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
            console.log(info.file.name);
            state.fileName = info.file.name;
            message.success(`${info.file.name} file uploaded successfully`);
            // Send uploaded
            sendFile()
                .then(readFile)
                .then(refresh);
                // .then(callSeparateQuestion);

        } else if (info.file.status === 'error') {
            console.log(info.file.name);
            message.error(`${info.file.name} file upload failed.`);
        }
    }

    const refresh = () => {
        props.refreshCallback();
    }

    /**
     * Remove the previously uploaded file to upload a new file.
     */
    const onRemove = () => {
        state.file = "";
        state.functionalButton = 'none';
    }

    /**
     * onDownload(fileType) is used to handle download request, both raw Markdown file and static HTML file.
     * It is decided by fileType.
     * @param fileId
     * @param fileName
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

        if (fileType === "raw") exportRaw(state.fileName, state.rawString);
        else if (fileType === "HTML") exportRaw(`${state.fileName}.html`, state.marpitResult);
        else console.log("Wrong fileType provided")
    }

    /**
     * send markdown file to backend and set the database returned fileId to state
     */
    const sendFile =() => {
        var file = state.file;
        var p = new Promise((resolve, reject) => {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', localStorage.getItem("instructorId"));
            console.log("Send data to backend", formData);
            axios.post(BASE_URL + "/upload", formData)
                .then(res => {
                    console.log("CCC",res.data);
                    state.fileId = res.data.fileId;
                    resolve(res.data.fileId);
                    // alert("File uploaded successfully.");
                })
                .catch((error) => {
                    reject(error);
                });
        });
        return p;
    }

    /**
     * This is a function that read the uploaded file into a string.
     * @returns {Promise<unknown>}
     */
    const readFile=()=>{
        var file = state.file;
        var p = new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsText(file);
            reader.onload = (e) => {
                // let content = e.target.result;
                state.rawString = reader.result;
                resolve(reader.result);
            };
            reader.onerror = function (e) {
                reject(e);
            };
        });
        return p;
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

                    <Button>
                        <Icon type = 'upload' /> Upload form local
                    </Button>

                </Upload>
            </div>

            <Link to={{pathname: '/EditPage', query: state.data}}>
                <Button size={"median"}
                        onClick={() => {
                            if (localStorage.getItem("saved") === "true" || !localStorage.hasOwnProperty('saved')) {
                                localStorage.setItem("fileId", "null");
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