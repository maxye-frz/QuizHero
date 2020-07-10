import React from "react";
import axios from "axios";
import {BASE_URL} from "../config/config";
import {message} from "antd";

class GithubLogin extends React.Component {
    componentDidMount() {
        let params = {
            login : "1",
        }
        axios
            .get(BASE_URL + "/github", {params})
            .then((res) => {
                // console.log("HTTP get request!");
                // localStorage.setItem("instructorId", res.data.userId);
                // localStorage.setItem("username", res.data.name);
                // localStorage.setItem("githubId", res.data.githubId);
                // localStorage.setItem("isLogin", "1");
                // console.log("after setItem");
                message.loading(
                    "Login success, directing you to HomePage",
                    [0.1],
                    () => {
                        window.location = '/HomePage';
                    });
            })
    }

    render() {
        return(
            <div/>
        )
    }
}

export default GithubLogin