import React, { Component } from 'react';
import {Button, Layout, List, Menu, message} from "antd";
import Slides from "../components/SpectaclePresenter";
import {BASE_URL} from "../config/config"
import {Link} from "react-router-dom";
import Marpit from "@marp-team/marpit";
import defaultTheme from "../components/default_theme/marpit-theme";
import {Markdown} from "../lib_presenter";
import Realtime from "./Realtime";
import separateQuestion from "../components/Parse";
const { Header } = Layout;

class EditPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            rawSting: 'Please write an essay about your favorite DOM element.'
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({rawString: event.target.value});
        var data = separateQuestion(event.target.value);
        data = JSON.stringify(data);
        localStorage.setItem("data", data)
        console.log(this.state.rawString);
    }

    handleSubmit(event) {
        alert('A name was submitted: ' + this.state.rawString);
        event.preventDefault();
    }

    marpitConvert =()=>{
        // 1. Marpit
        const marpit = new Marpit();
        // 2. Add Marpit theme CSS
        const theme = defaultTheme;

        marpit.themeSet.default = marpit.themeSet.add(theme);

        /**
         * render markdown using marpit
         */
        const {html, css} = marpit.render(this.state.rawString);
        console.log("marpit", html, css);

        this.setState({
            html : html,
            css : css,
        });
        /**
         * create filestring to store HTML string
         * @type {string}
         */
        // let filestring = `
        // <!DOCTYPE html>
        // <html><body>
        //   <style>${css}</style>
        //   ${html}
        // </body></html>
        // `
        // ;
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
                    <Menu theme="white" mode="horizontal" defaultSelectedKeys={['2']}>

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

                <div className={"divleft"} style={{paddingTop:"50px"}}>
                    <form
                          onSubmit={this.handleSubmit}>

                        <input type="submit" value="Submit" />

                        <textarea className={"inputtext"}
                                  value={this.state.rawString} onChange={this.handleChange} />

                    </form>
                </div>

                <Realtime/>

                {/*<div className={"divright"} style={{paddingTop:"50px"}}>*/}
                {/*    this is realtime render*/}
                {/*    /!*<form*!/*/}
                {/*    /!*    onSubmit={this.handleSubmit}>*!/*/}

                {/*    /!*    <input type="submit" value="Submit" />*!/*/}

                {/*    /!*    /!*<textarea className={"inputtext"}*!/*!/*/}
                {/*    /!*    /!*          value={this.state.rawString} onChange={this.handleChange} />*!/*!/*/}
                {/*    <Realtime />*/}
                {/*    */}
                {/*    /!*</form>*!/*/}

                {/*    /!*<Markdown containsSlides>{this.state.rawString}</Markdown>*!/*/}
                {/*    /!*<html><body>*!/*/}
                {/*    /!*<style>${this.state.css}</style>*!/*/}
                {/*    /!*${this.state.html}*!/*/}
                {/*    /!*</body></html>*!/*/}
                {/*</div>*/}

            </div>
        )
    }
}

export default EditPage;