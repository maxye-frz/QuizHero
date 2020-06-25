import React, {Component} from "react";
import {Form, Input, Button, Checkbox, message, Alert} from "antd";
import {useHistory} from "react-router-dom";
import {UserOutlined, LockOutlined, WindowsOutlined} from "@ant-design/icons";
import {connect} from "react-redux";
import {userLoginAction} from "../store/actions/loginActions";
import "../style/loginPageStyle.css";
import logo from "../fig/logo.png";
import axios from "axios";
import {BASE_URL} from "../config/config";

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
        if (localStorage.getItem("isLogin") === '1') window.location = "/HomePage";

        if (localStorage.getItem("isGithubLogin") === '1') {
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

        var bcrypt = require('bcryptjs');
        const saltRounds = 10;
        const myPlaintextPassword = 's0/\/\P4$$w0rD';
        // const someOtherPlaintextPassword = 'not_bacon';

        // async
        // generate a salt and hash on separate function calls
        bcrypt.genSalt(saltRounds, function(err, salt) {
            console.log(salt);
            bcrypt.hash(myPlaintextPassword, salt, function(err, hash) {
                // Store hash in your password DB.
                console.log(hash);
                bcrypt.compare(myPlaintextPassword, hash, function(err, result) {
                    // result == true
                    console.log(result)
                });
            });
        });
        // // auto-gen a salt and hash
        // bcrypt.hash(myPlaintextPassword, saltRounds, function(err, hash) {
        //     // Store hash in your password DB.
        // });
        // Load hash from your password DB.
        // bcrypt.compare(myPlaintextPassword, hash, function(err, result) {
        //     // result == true
        //     console.log(result)
        // });

        // // sync
        // // generate a salt and hash on separate function calls
        // const salt = bcrypt.genSaltSync(saltRounds);
        // const hash = bcrypt.hashSync(myPlaintextPassword, salt);
        // // Store hash in your password DB.
        // // auto-gen a salt and hash
        // const hash = bcrypt.hashSync(myPlaintextPassword, saltRounds);
        // // Store hash in your password DB.

        const formData = new FormData();
        formData.append("email", email);
        formData.append("pswd", password);

        axios
            .post(BASE_URL + "/login", formData)
            .then((res) => {
                console.log(res.status);
                if (res.status === 201) {
                    console.log("Login success");
                    message.loading(
                        "Login success, directing you to HomePage",
                        [2],
                        (onclose = () => {
                            this.props.login(res.data.name, res.data.userId);
                            localStorage.setItem("instructorId", res.data.userId);
                            localStorage.setItem("username", res.data.name);

                            localStorage.setItem("isLogin", 1);

                            // window.location = "/login"
                            // history.push("/HomePage");
                        })
                    );
                }
            })
            .catch((err) => {
                console.log(err);
                message.error(
                    "Log in failed. Please check your account and password and try again!"
                );
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
        console.log("go to github!!!!!!!!!!!")
        // let params
        localStorage.setItem("isGithubLogin", '1');
        // window.location = '/login';
        // axios
        //     .get(BASE_URL + "/github")
        //     .then((res) => {
        //         if (res.status === 200) {
        //             console.log(res);
        //             console.log("HTTP get request!");
        //             localStorage.setItem("instructorId", res.data.userId);
        //             localStorage.setItem("username", res.data.name);
        //             localStorage.setItem("githubId", res.data.githubId);
        //             // localStorage.setItem("isLogin", "1");
        //             console.log("after setItem");
        //             // window.location = '/HomePage';
        //         }
        //     })
    }

    getGithubLoginInfo = () => {
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
                            className="login-form-button"
                            onClick={this.handleSubmit}
                        >
                            Log in
                        </Button>
                        <Button
                            type="primary"
                            className="register-button"
                            onClick={this.registerButtonHandler}
                        >
                            Register
                        </Button>
                        <Button onClick={this.loginWithGitHub}>
                            Login with GitHub
                        </Button>
                        <Button onClick={this.getGithubLoginInfo}>
                            Get GitHub Login Info
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
