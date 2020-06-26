import React, { Component } from 'react';
import {Link} from "react-router-dom";
import {Button, Layout, Menu, message} from "antd";
import Realtime from "../components/realtime_components/Realtime";
import axios from "axios";
import {BASE_URL} from "../config/config";
const { Header } = Layout;

class EditPage extends React.Component {
    constructor(props) {
        super(props);
    }

    /**
     * Clear localStorage in browser when logout.
     */
    handleLogOut(){
        localStorage.setItem("username", null);
        localStorage.setItem("instructorId", '0');
        localStorage.setItem("githubId", '0');
        localStorage.setItem("isGithubLogin", '0');
        localStorage.setItem("isLogin", '0');
        localStorage.setItem("data", null);
        axios.get(BASE_URL + "/logout")
            .then(() => {
                message.loading('Local logout!', [0.1], () => {window.location = "/login";});
            })
    }

    render() {
        const username = localStorage.getItem("username")?localStorage.getItem("username"):"";

        const logOutBtnStyle = {
            background: "none",
            border: "none",
            paddingLeft: "7px",
            color: "#1890FF",
            textDecoration: "underline",
            cursor: "pointer"
        };
        /**
         * Render NavBar and text editor.
         */
        return (
            <div className="App">
                <Header style={{height: 0, padding: 0, position: 'fixed', zIndex: 1, width: '100%' }}>
                    <div className="logo" />
                    <Menu theme="white" mode="horizontal" defaultSelectedKeys={['3']}>

                        <Menu.Item key="1" style={{display:"inline-block",float:"left", marginLeft:"30px", width: "150px"}}>
                            <Link to={'/HomePage'}>Home</Link>
                        </Menu.Item>
                        {/*<Menu.Item key="2" style={{display:"inline-block",float:"left", width: "150px"}}>*/}
                        {/*    <Link to={'/HistoryPage'}>History</Link>*/}
                        {/*</Menu.Item>*/}
                        <Menu.Item key="3" style={{display:"inline-block",float:"left", width: "150px"}}>
                            <Link to={'/EditPage'}>Edit</Link>
                        </Menu.Item>

                        <div style={{display:"inline-block",float:"right",paddingRight:"60px"}}>
                            Welcome, {username}
                            <button onClick={this.handleLogOut} style={logOutBtnStyle}>Log Out</button>
                        </div>

                    </Menu>
                </Header>

                <Realtime />

            </div>
        )
    }
}

export default EditPage;