import React, { Component } from 'react';
import {Link} from "react-router-dom";
import {Button, Layout, Menu, message} from "antd";
import Realtime from "../components/realtime_components/Realtime";
import jwt_decode from "jwt-decode";
import cookie from "react-cookies";
import {handleLogout} from "../utils/handleLogout";

const { Header } = Layout;


class EditPage extends React.Component {
    constructor(props) {
        super(props);
    }


    render() {
        const loginInfo = jwt_decode(cookie.load('token'));
        const username = loginInfo['name'];

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
                            <button onClick={handleLogout} style={logOutBtnStyle}>Log Out</button>
                        </div>

                    </Menu>
                </Header>

                <Realtime />

            </div>
        )
    }
}

export default EditPage;