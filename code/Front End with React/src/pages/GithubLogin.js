import React from "react";
import axios from "axios";
import {BASE_URL} from "../config/config";

class GithubLogin extends React.Component {
    componentDidMount() {
        axios
            .get(BASE_URL + "/github")
            .then((res) => {
                console.log("HTTP get request!");
                localStorage.setItem("instructorId", res.data.userId);
                localStorage.setItem("username", res.data.name);
                localStorage.setItem("githubId", res.data.githubId);
                localStorage.setItem("isLogin", "1");
                console.log("after setItem");
                window.location = '/HomePage';
            })
    }

    render() {
        return(
            <div>
                <div>
                    {localStorage.getItem("instructorId")}
                </div>
                <div>
                    {localStorage.getItem("username")}
                </div>
                <div>
                    {localStorage.getItem("githubId")}
                </div>
            </div>
        )
    }
}

export default GithubLogin