import React, {Component} from "react";
import {Form, Input, Button, Checkbox, message, Alert} from "antd";
import {useHistory} from "react-router-dom";
import {UserOutlined, LockOutlined, WindowsOutlined, GithubOutlined} from "@ant-design/icons";
import {connect} from "react-redux";
import {userLoginAction} from "../store/actions/loginActions";
import "../style/loginPageStyle.css";
import logo from "../fig/logo.png";
import axios from "axios";
import {BASE_URL} from "../config/config";
import {isLogin} from "../utils/isLogin";

/**
 * Map the state from reducer to the props of the component
 * to get the data from the store
 *
 * @param {object}  state from reducer
 */
const mapStateToProps = (state) => {
    return {
        instructorId: state.setUserName.instructorId,
        username: state.setUserName.username,
    };
};

/**
 * Map dispatch to the props of the component
 *
 * @param {dispatch}  for dispatch of action
 */
const mapDispatchToProps = (dispatch) => {
    return {
        login: (username, instructorId) => {
            dispatch(userLoginAction(username, instructorId));
        },
        // loadMechanicsWork: () => {
        //     dispatch(mechanicWorkAction({"infotype":"50"}));
        // },
    };
};

/**
 * Page for user login
 *
 * @param {object} props Component props
 */
class LoginPage extends Component {
    formRef = React.createRef();

    componentDidMount() {
        if (isLogin()) window.location = '/HomePage';
    }

    /**
     * Form button listener, triggered when the login form is submitted
     * by the button
     *
     * @param {object} event
     */
    handleSubmit = () => {
        // let history = useHistory();
        const {history} = this.props;
        let email = this.props.form.getFieldValue("email")
            ? this.props.form.getFieldValue("email")
            : null;
        let password = this.props.form.getFieldValue("password")
            ? this.props.form.getFieldValue("password")
            : null;
        console.log(email);
        console.log(password);

        if (email === null || password === null) {
            message.error("All fields must be filled");
            return;
        }

        const formData = new FormData();
        formData.append("email", email);
        axios
            .post(BASE_URL + "/emailForPassword", formData)
            .then((res) => {
                let bcrypt = require('bcryptjs');
                bcrypt.compare(password, res.data.pswd, function(err, result) {
                    // result == true
                    console.log(result)
                    if (result === true) {
                        axios
                            .post(BASE_URL + "/login", formData)
                            .then((res) => {
                                console.log(res.status);
                                if (res.status === 201) {
                                    console.log("Login success");
                                    message.loading(
                                        "Login success, directing you to HomePage",
                                        [0.1],
                                        () => {window.location = "/HomePage";}); // history.push("/HomePage");
                                }
                            })
                            .catch((err) => {
                                console.log(err);
                            });
                    } else {
                        // Wrong Password
                        message.error("Log in failed. Please check your account and password and try again!");
                    }
                });
            })
            .catch((err) => {
                // No account
                console.log(err);
                message.error("Log in failed. Please check your account and password.\n\n" +
                    "If do not have an account, please register first!");
            });

    };

    /**
     * Register button listener, triggered when the register button is pressed
     * The page will be redirect to the registration page
     *
     * @param {object} event
     */
    registerButtonHandler = () => {
        window.location = "/register";
    };

    loginWithGitHub = () => {
        // const { history } = this.props;
        window.location = BASE_URL + "/github";
    }

    render() {
        const {getFieldProps} = this.props.form;
        const {instructorId, username} = this.props;

        console.log("Name", username);
        console.log("ID", instructorId);

        return (
            <div className="login-page-container">
                <img src={logo} className="App-logo" alt="logo"/>
                <Form
                    name="normal_login"
                    className="login-form"
                    initialValues={{
                        remember: true,
                    }}
                    ref={this.formRef}
                >
                    <Form.Item
                        name="username"
                        rules={[
                            {
                                required: true,
                                message: "Please input your Email!",
                            },
                        ]}
                    >
                        <Input
                            className="login-input"
                            prefix={<UserOutlined className="site-form-item-icon"/>}
                            placeholder="Email"
                            {...getFieldProps("email")}
                        />
                    </Form.Item>
                    <Form.Item
                        name="password"
                        rules={[
                            {
                                required: true,
                                message: "Please input your Password!",
                            },
                        ]}
                    >
                        <Input
                            className="login-input"
                            prefix={<LockOutlined className="site-form-item-icon"/>}
                            type="password"
                            placeholder="Password"
                            {...getFieldProps("password")}
                        />
                    </Form.Item>
                    <Form.Item>
                        <Button
                            type="primary"
                            className="github-login-button"
                            size={"large"}
                            onClick={this.loginWithGitHub}>
                            <GithubOutlined />Login with GitHub
                        </Button>
                        <Button
                            type="primary"
                            className="login-form-button"
                            size={"large"}
                            onClick={this.handleSubmit}
                        >
                            Log in
                        </Button>
                        <Button
                            type="primary"
                            className="register-button"
                            size={"large"}
                            onClick={this.registerButtonHandler}
                        >
                            Register
                        </Button>
                    </Form.Item>
                </Form>
            </div>
        );
    }
}

export default Form.create({name: "LoginPage"})(
    connect(mapStateToProps, mapDispatchToProps)(LoginPage)
);
