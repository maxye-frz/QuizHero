/**
 * UploadPage renders the page where the presenter can upload his/her markdown file.
 */

import {Button, Icon, Layout, Menu, message, Upload} from 'antd';
import React from "react";
import marpitConvert from '../components/Marpit'
import separateQuestion from "../components/Parse";
import axios from 'axios';
import {BASE_URL} from "../config/config"
import {Link} from "react-router-dom"
import {CopyToClipboard} from 'react-copy-to-clipboard'
import logo from "../fig/logo.png"

const { Header } = Layout;
const props = {
    name: 'file',
    action: 'https://www.mocky.io/v2/5cc8019d300000980a055e76',
    headers: {
        authorization: 'authorization-text',
    },
};

class MyUpload extends React.Component{
    // constructor(props) {
    //     super(props);
    // }
    state = {
        file:"",
        fileId:"",
        fileName:"",
        rawString:"",
        data:"",
        marpitResult:"",
        functionalButton:'none'
    }

    /**
     * Catch the uploaded file and handle multiple uploads error.
     * @param file
     * @returns {boolean}
     */
    beforeUpload = (file) => {
        if (this.state.file === ""){
            console.log("FILEEE",file);
            this.setState({
                file:file
            });
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
    onChange = (info) => {
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
            this.setState({
                fileName :info.file.name
            });
            message.success(`${info.file.name} file uploaded successfully`);
            // Send uploaded
            this.sendFile()
                .then(this.readFile)
                .then(this.callSeparateQuestion);
            this.setState({functionalButton : 'block'});
            // this.state.functionalButton = this.showFunctionalButton('block');

        } else if (info.file.status === 'error') {
            console.log(info.file.name);
            message.error(`${info.file.name} file upload failed.`);
        }
    }

    /**
     * Remove the previously uploaded file to upload a new file.
     */
    onRemove = () => {
        console.log(this.state.file);
        this.setState({
            file: "",
            functionalButton: 'none'
        }, () => console.log(this.state.file) );

        // this.state.functionalButton = this.showFunctionalButton('none');
    }

    /**
     * onDownload(fileType) is used to handle download request, both raw Markdown file and static HTML file.
     * It is decided by fileType.
     * @param fileType
     */
    onDownload = (fileType) => {
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

        if (fileType === "raw") exportRaw(this.state.fileName, this.state.rawString);
        else if (fileType === "HTML") exportRaw(`${this.state.fileName}.html`, this.state.marpitResult);
        else console.log("Wrong fileType provided")
    }

    /**
     * send markdown file to backend and set the database returned fileId to state
     */
    sendFile = () => {
        var file = this.state.file;
        return new Promise((resolve, reject) => {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', localStorage.getItem("instructorId"));
            console.log("Send data to backend", formData);
            axios.post(BASE_URL + "/upload", formData)
                .then(res => {
                    console.log("CCC", res.data);
                    this.setState({fileId: res.data.fileId})
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
    readFile = () => {
        var file = this.state.file;
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsText(file);
            reader.onload = (e) => {
                // let content = e.target.result;
                this.setState({rawString: reader.result});
                resolve(reader.result);
            };
            reader.onerror = function (e) {
                reject(e);
            };
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
     */
    callSeparateQuestion = () => {
        var data = separateQuestion(this.state.rawString, this.state.fileId);
        console.log(data);
        data.fileId = this.state.fileId;
        data = JSON.stringify(data);
        localStorage.setItem("data",data);
        this.setState({data : data});
        this.getMarpit();
    }

    /**
     *  call marpitConvert in Marpit.js for downloading static HTML
      */
    getMarpit = () => {
        const marpitResult = marpitConvert(this.state.rawString)
        this.setState({
            marpitResult : marpitResult
        })
    }

    /**
     * startSharing(fileId) is a function for presenter to open the sharing permission.
     */
    startSharing = () => {
        const formData = new FormData();
        formData.append('fileId', this.state.fileId);
        formData.append('permission', true);
        axios.post(BASE_URL + "/filepermission", formData)
            .then(()=> message.success(`Share code ${this.state.fileId} is copied on your clipboard`))
            .catch(()=> message.error('error'));
    }

    /**
     * stopSharing(fileId) is a function for presenter to stop sharing.
     */
    stopSharing = () => {
        const formData = new FormData();
        formData.append('fileId', this.state.fileId);
        formData.append('permission', false);
        axios.post(BASE_URL + "/filepermission", formData)
            .then(()=> message.success(`File ${this.state.fileId} stop sharing`))
            .catch(()=> message.error('error'));
    }

    /**
     * Show or hide the button depending on whether there's a file uploaded or not.
     * @param status
     */
    // showFunctionalButton (status) {
    //     this.setState({
    //         functionalButton:status
    //     })
    // };

    /**
     * Clear localStorage in browser when logout.
     */
    handleLogOut() {
        localStorage.setItem("username",null)
        localStorage.setItem("instructorId",0)
        localStorage.setItem("isLogin",0)
        localStorage.setItem("data", null)
        window.location = "/login"
    }

    /**
     * return rendered UploadPage
     */
    render() {
        const username = localStorage.getItem("username") ? localStorage.getItem("username") : "";

        const logOutBtnStyle = {
                background: "none",
                border: "none",
                paddingLeft: "7px",
                color: "#1890FF",
                textDecoration: "underline",
                cursor: "pointer"
        };
        /**
         * Render NavBar and all other buttons on upload page.
         */
        return(
            <div className="App">
                <Header style={{height: 0, padding: 0, position: 'fixed', zIndex: 1, width: '100%' }}>
                    <div className="logo" />
                    <Menu theme="white" mode="horizontal" defaultSelectedKeys={['1']}>

                        <Menu.Item key="1" style={{display:"inline-block",float:"left", marginLeft:"30px", width: "150px"}}>
                            <Link to={'/HomePage'}>Home</Link>
                        </Menu.Item>
                        <Menu.Item key="2" style={{display:"inline-block",float:"left", width: "150px"}}>
                            <Link to={'/HistoryPage'}>History</Link>
                        </Menu.Item>
                        <Menu.Item key="3" style={{display:"inline-block",float:"left", width: "150px"}}>
                            <Link to={'/EditPage'}>Edit</Link>
                        </Menu.Item>
                        
                        <div style={{display:"inline-block",float:"right",paddingRight:"60px"}}>
                            Welcome, {username}
                            <button onClick={this.handleLogOut} style={logOutBtnStyle}>Log Out</button>
                        </div>

                    </Menu>
                </Header>
                
                <div className="App-header">
                    
                    <img src={logo} className="App-logo" alt="logo"/>

                    <div>
                        {/* Upload button*/}
                        <div>
                            <Upload
                                onChange={this.onChange}
                                beforeUpload={this.beforeUpload}
                                onDownload={() => this.onDownload("raw")}
                                onPreview={() => this.onDownload("raw")}
                                onRemove={this.onRemove}
                                {...props}>

                                <Button>
                                    <Icon type = 'upload' /> Click to Upload
                                </Button>

                            </Upload>
                        </div>
                        {/*Presenter/Student mode button*/}
                        <div style={{display:this.state.functionalButton}}>
                            <Link to={{pathname: '/presenter', query: this.state.data}} target = '_blank'>
                                <Button size={"median"} style={{marginRight: 10}}>
                                    Presenter mode
                                </Button>
                            </Link>
                            <Button size={"median"} style={{marginLeft: 10}}
                                    onClick={() => this.onDownload("HTML")}>
                                Download HTML
                            </Button>
                        </div>
                        {/*Start/Stop sharing file button*/}
                        <div style={{display:this.state.functionalButton}}>
                            <CopyToClipboard
                                onCopy={this.startSharing}
                                text={this.state.fileId}>
                                <Button size={"median"} style={{marginRight: 10}}>
                                    Start sharing
                                </Button>
                            </CopyToClipboard>
                            <Button size={"median"} style={{marginLeft: 10}}
                                    onClick={this.stopSharing}>
                                Stop sharing
                            </Button>
                        </div>
                    </div>

                    <div>
                        <Link to={{pathname: '/EditPage', query: this.state.data}}>
                            <Button size={"median"}
                                    onClick={() => {
                                        if (localStorage.getItem("saved") === "true" || !localStorage.hasOwnProperty('saved')) {
                                            localStorage.setItem("fileId", "null");
                                            localStorage.setItem("newFileName", "");
                                            localStorage.setItem("newFileString", "");
                                        } else {
                                            alert("There are unsaved changes, please save them first!");
                                        }}}>
                                    {/*pre-set newFileString to "", because Marpit cannot take null input*/}
                                New File
                            </Button>
                        </Link>
                    </div>

                </div>

            </div>
        )
    }
}

export default MyUpload;