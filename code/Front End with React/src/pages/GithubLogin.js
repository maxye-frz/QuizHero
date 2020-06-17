import React from "react";
import axios from "axios";
import {BASE_URL} from "../config/config";

class GithubLogin extends React.Component {
    componentDidMount() {
        axios
            .post(BASE_URL + "/githublogin")
            .then((res) => {
                console.log("HTTP get request!");
                localStorage.setItem("instructorId", res.userId);
                localStorage.setItem("username", res.name);
                localStorage.setItem("githubId", res.githubId);
                console.log("after setItem");
                window.location("/HomePage");
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