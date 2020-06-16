/**
 * StudentRequestPage is used for student to input shared code and go to corresponding presentation.
 */

import React, { Component } from "react";
import {Input, Button, message} from "antd";
import {Link} from "react-router-dom"
import logo from "../fig/logo.png";
import axios from "axios";
import {BASE_URL} from "../config/config";
import separateQuestion from "../components/Parse";
const { Search } = Input;

class StudentRequestPage extends Component{
    constructor(props) {
        super(props);

        this.state = {
            display_name:'none'
        }
    }

    /**
     * Search the input shared code. If the presenter release the permission, go and call fetchFile(); else alert error.
     * @param value
     */
    onSearch = (value) => {
        let params = {
            fileId: value
        };

        axios.get(BASE_URL + "/filepermission",  {params})
            .then(res => {
                console.log("AAA", res.data);
                if (res.data) {
                    this.setState({display_name: 'block'});
                    this.fetchFile(value);
                } else {
                    this.setState({display_name : 'none'});
                    alert(`Sorry, you don't have the permission to access file ${value}. Please contact the presenter.`)
                }

            })
            .catch((error) => {
                alert(`File doesn't exist! ${error}`)
            })

    }

    /**
     * fetchFile from the back end and callSeparateQuestion(rawString) to prepare for the presentation.
     */
    fetchFile = (fileId) => {
        let params = {
            fileId: fileId
        }

        axios.get(BASE_URL + "/fetch",  {params})
            .then(res => {
                console.log("AAA", res.data);
                this.callSeparateQuestion(res.data);
                message.success(`File ${this.state.fileId} fetched successfully.`)
            })
            .catch((error) => {
                alert(`Fail to fetch File ${this.state.fileId}. ${error}`)
            })
    }

    /**
     * callSeparateQuestion(rawString) is a helper function to call separateQuestion from Parse.js
     * separateQuestion(rawString) will parse the raw string to a JSON parameter which contains quizzes and slides.
     * data = {
     *     fileId : fileId,
     *     quiz : [],
     *     slidesString : []
     * }
     * which will be set to localStorage in browser, which will be used in PresenterPage.js
     * @param rawString
     */
    callSeparateQuestion = (rawString) =>{
        var data = separateQuestion(rawString);
        data.fileId = this.state.fileId;
        data = JSON.stringify(data);
        localStorage.setItem("data", data);
        // this.setState({display_name: 'block'});
    }

    /**
     * return rendered StudentRequestPage. Use <Search/> from "antd" as the search bar.
     */
    render() {
        return (
            <div className="App">
                <header className="App-header">

                    <img src={logo} className="App-logo" alt="logo"/>
                        <Search
                            style={{width: 400}}
                            placeholder="input shared code"
                            enterButton="Search"
                            size="large"
                            onSearch={this.onSearch}
                        />
                    <div style={{display: this.state.display_name}}>
                        <Link to={{pathname: '/student', query: this.state.data}} target = '_blank'>
                            <Button size={"large"} style={{marginLeft: 10}}>
                                Go to Presentation
                            </Button>
                        </Link>
                    </div>
                </header>
            </div>
        );
    }
}

export default StudentRequestPage;