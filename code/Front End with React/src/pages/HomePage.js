/**
 * The HomePage is the history page for the login user (presenter), where the user can open the previous presentation,
 * download raw Markdown file and static HTML file, delete the presentation from the database and control the sharing permission.
 */

import React from 'react';
import 'antd/dist/antd.css';
import '../App.css'
import {Link} from "react-router-dom";
import { Menu, Layout, message } from 'antd';
import Home from "../components/home_componets/Home";
import axios from "axios";
import {BASE_URL} from "../config/config";
import {useCookies} from "react-cookie";
import jwt_decode from "jwt-decode";

const { Header } = Layout;


class HomePage extends React.Component {

    const cookies = getCookies(['token']);
    var decoded = jwt_decode(cookies);
    console.log(decoded);

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

    /**
     * return rendered HomePage page. Use <List.Item/> to show the list of uploaded files.
     */
    render() {

        // const { fileList } = this.state;

        const username = localStorage.getItem("username") ? localStorage.getItem("username"):"";

        const logOutBtnStyle = {
            background: "none",
            border: "none",
            paddingLeft: "7px",
            color: "#1890FF",
            textDecoration: "underline",
            cursor: "pointer"
        };

        return (
            <div className="App">
                <Header style={{height: 0, padding: 0, position: 'fixed', zIndex: 1, width: '100%' }}>
                    {/*<img src={logo} className="logo" alt="logo"/>*/}
                    <Menu theme="white" mode="horizontal" defaultSelectedKeys={['1']}>

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

                <div style={{paddingRight: 20, paddingTop: 60, display: "flex"}}>
                    <Home />
                </div>

            </div>

        );
    }
}

export default HomePage;