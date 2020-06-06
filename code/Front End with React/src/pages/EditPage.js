import React, { Component } from 'react';
import {Button, Layout, List, Menu, message} from "antd";
import {BASE_URL} from "../config/config"
import {Link} from "react-router-dom";
import Realtime from "../components/Realtime";
const { Header } = Layout;

class EditPage extends React.Component {
    constructor(props) {
        super(props);
    }

    handleSubmit(event) {
        alert('A name was submitted: ' + this.state.rawString);
        event.preventDefault();
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
                <Header style={{height: 50, padding: 0, position: 'fixed', zIndex: 1, width: '100%' }}>
                    <div className="logo" />
                    <Menu theme="white" mode="horizontal" defaultSelectedKeys={['3']}>

                        <Menu.Item key="1" style={{display:"inline-block",float:"left", marginLeft:"30px", width: "150px"}}>
                            <Link to={'/HomePage'}>Upload</Link>
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

                <Realtime/>

            </div>
        )
    }
}

export default EditPage;