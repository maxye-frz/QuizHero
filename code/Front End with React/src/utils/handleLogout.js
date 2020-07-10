import cookie from "react-cookies";
import axios from "axios";
import {BASE_URL} from "../config/config";
import {message} from "antd";

/**
 * Clear cookie in browser when logout.
 */
export const handleLogout = () => {
    // localStorage.setItem("username", null);
    // localStorage.setItem("instructorId", '0');
    // localStorage.setItem("githubId", '0');
    // localStorage.setItem("isGithubLogin", '0');
    // localStorage.setItem("isLogin", '0');
    cookie.remove('token');
    localStorage.setItem("data", null);
    axios.get(BASE_URL + "/logout")
        .then(() => {
            message.loading('Local logout!', [0.1], () => {window.location = "/login";});
        })
}