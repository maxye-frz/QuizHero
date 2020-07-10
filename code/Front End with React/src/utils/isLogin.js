import cookie from "react-cookies";

/**
 * return login status
 * @returns {boolean}
 */

export const isLogin = () => {
    let isLogin = cookie.load('token');
    if (isLogin) {
        return true;
    }else{
        console.log("Return false", isLogin)
        return false;
    }
}