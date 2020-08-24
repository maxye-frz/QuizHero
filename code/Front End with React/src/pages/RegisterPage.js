import React, {Component} from "react";
import {Form, Input, Button, Checkbox, message} from "antd";
import {UserOutlined, LockOutlined, MailOutlined} from "@ant-design/icons";
import {connect} from "react-redux";
import "../style/loginPageStyle.css";
import {BASE_URL} from "../config/config";
import logo from "../fig/logo.png";
import axios from "axios";

/**
 * Page for user registration
 *
 * @param {object} props Component props
 */
class RegisterPage extends Component {
    onFinish = (values) => {
        console.log("Received values of form: ", values);
    };

    /**
     * Form button listener, triggered when the register form is submitted
     * by the button
     *
     * @param {object} event
     */
    handleSubmit = (err, values) => {
        let username = this.props.form.getFieldValue("username")
            ? this.props.form.getFieldValue("username")
            : null;
        let email = this.props.form.getFieldValue("email")
            ? this.props.form.getFieldValue("email")
            : null;
        let password = this.props.form.getFieldValue("password")
            ? this.props.form.getFieldValue("password")
            : null;

        if (email === null || password === null || username === null) {
            message.error("All fields must be filled");
            return;
        }
        var regEmail = new RegExp(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\\])"
        );
        if (!regEmail.test(email)) {
            message.error("Please enter email in correct format!");
            return;
        }

        if (password.length < 6) {
            message.error("Password must be at least 6 characters!");
            return;
        }

        var bcrypt = require('bcryptjs');
        const saltRounds = 10;

        // async
        // generate a salt and hash on separate function calls
        bcrypt.genSalt(saltRounds, function(err, salt) {
            console.log(salt);
            bcrypt.hash(password, salt, function(err, hash) {
                // Store hash in your password DB.
                console.log(hash);

                let params = {
                    name: username,
                    email: email,
                    pswd: hash,
                    salt: "salt",
                };

                axios
                    .post(BASE_URL + "/register", params)
                    .then((res) => {
                        console.log(res.status);
                        if (res.status === 201) {
                            message.loading(
                                "Register success, auto login",
                                [0.1],
                                onclose = () => {
                                    const formData = new FormData();
                                    formData.append("email", email);
                                    axios.post(BASE_URL + "/login", formData)
                                        .then((res) => {
                                            console.log(res.status);
                                            if (res.status === 201) {
                                                console.log("Login success");
                                                message.loading(
                                                    "Login success, directing you to HomePage",
                                                    [0.1],
                                                    () => {window.location = "/HomePage";});
                                            }
                                        })
                                        .catch((err) => {
                                            console.log(err);
                                        });
                                }
                            );
                        }
                    })
                    .catch((err) => {
                        console.log(err);
                        message.error(
                            "Account already exists. Please use another email address!"
                        );
                    });
            });
        });

    };

    /**
     * Back button listener, triggered when the back button is pressed
     * The page will be redirect to the login page
     *
     * @param {object} event
     */
    backButtonHandler = () => {
        // window.location.replace(BASE_URL + "/login");
        window.location = "/login";
    };

    render() {
        const {getFieldProps} = this.props.form;

        const validateMessages = {
            required: ` is required!`,
            types: {
                email: ` is not validate email!`,
            },
        };

        return (
            <div className="login-page-container">
                <img src={logo} className="App-logo" alt="logo"/>
                <Form
                    name="normal_login"
                    className="login-form"
                    initialValues={{
                        remember: true,
                    }}
                    onFinish={this.onFinish}
                    validateMessages={validateMessages}
                >
                    <Form.Item
                        name="username"
                        rules={[
                            {
                                required: true,
                                message: "Please input your Username!",
                            },
                        ]}
                    >
                        <Input
                            className="login-input"
                            prefix={<UserOutlined className="site-form-item-icon"/>}
                            placeholder="Username"
                            {...getFieldProps("username")}
                        />
                    </Form.Item>
                    <Form.Item
                        name="email"
                        rules={[
                            {
                                required: true,
                                message: "Please input your email!",
                            },
                            {
                                type: "email",
                                message: "Please use a valid email!",
                            },
                        ]}
                    >
                        <Input
                            className="login-input"
                            prefix={<MailOutlined className="site-form-item-icon"/>}
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

                    <Form.Item style={{marginTop: "20%"}}>
                        <Button
                            type="primary"
                            htmlType="submit"
                            className="register-button-real"
                            size={"large"}
                            onClick={this.handleSubmit}
                        >
                            Register
                        </Button>
                        <Button
                            type="primary"
                            className="register-button"
                            size={"large"}
                            onClick={this.backButtonHandler}
                        >
                            Back
                        </Button>
                    </Form.Item>
                </Form>
            </div>
        );
    }
}

export default Form.create({name: "LoginPage"})(RegisterPage)
