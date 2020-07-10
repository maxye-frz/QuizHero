import React, {useContext, useState, useLayoutEffect} from "react";
import ReactDOM from 'react-dom';
import styled from "styled-components";
import editorContext from "./editorContext";
import {Button, message} from "antd";
import {DeleteOutlined, SaveOutlined} from "@ant-design/icons";
import axios from "axios";
import {BASE_URL} from "../../config/config";
import realtimeTheme from "../default_theme/realtime-theme";

const Container = styled.div`
  width: calc(50vw);
  height: 100%;
  padding: 13px;
  border-right: 1.5px solid rgba(15, 15, 15, 0.4);
  font-family: "Lato", sans-serif;
`;

const Title = styled.div`
  // height: 60px;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 1em;
  padding: 6px 0;
  border-bottom: 1px solid rgba(15, 15, 15, 0.3);
  display: flex;
`;

const TitleArea = styled.textarea`
  width: 60%;
  height: 100%;
  font-size: 22px;
  font-weight: 600;
  resize: none;
  border: none;
  text-align: center;
`;

const TextArea = styled.textarea`
  width: 100%;
  height: 88%;
  resize: none;
  border: none;
  outline: none;
  overflow-y: scroll;
  font-size: 17px;
`;

export function InputCSS(props) {

    // Please do not delete this. get initial value from localStorage
    // these is for use inside of this component only, like state and setState in class
    // const [ fileName, setFileName ] = useState(localStorage.getItem("newFileName"));
    // const [ fileValue, setFileValue ] = useState(localStorage.getItem("newFileString"));

    // set them to context to share with render.js
    const { CSS, setCSS } = useContext(editorContext);

    console.log(props);
    // Initialization (only once)
    // 土办法
    // if (titleText === "") setTitleText(localStorage.getItem("newFileName"));
    // if (markdownText === "") setMarkdownText(localStorage.getItem("newFileString"));

    // Similar to componentDidMount and componentDidUpdate:
    // !localStorage.hasOwnProperty('saved')
    useLayoutEffect(() => {
        setCSS(realtimeTheme); //default
        if (localStorage.getItem("fileId") !== "null")
            readCSS();
    }, []);

    // const discard = () => {
    //
    //     if (localStorage.getItem("saved") === "true") {
    //         window.alert("You have not make any changes.")
    //     } else {
    //         const confirmDiscard = window.confirm("Are you sure to discard all the changes?");
    //         if (confirmDiscard === true) {
    //             localStorage.setItem("fileId", "null");
    //             localStorage.setItem("newFileName", "");
    //             localStorage.setItem("newFileString", "");
    //             localStorage.setItem("saved", "true");
    //             // setFileName("");
    //             // setFileValue("");
    //             setTitleText("");
    //             setMarkdownText("");
    //         }
    //     }
    // }

    const onInputChange = e => {
        const newValue = e.currentTarget.value;
        // localStorage.setItem("newFileString", newValue);
        localStorage.setItem("saved", "false");
        // setFileValue(newValue);
        // console.log(fileValue);
        setCSS(newValue);

    }

    const readCSS = () => {
        let params = {
            fileId: localStorage.getItem("fileId")
        }
        axios.get(BASE_URL + "/readCSS", {params})
            .then(res => {
                // localStorage.setItem("CSS", res.data);
                setCSS(res.data);
            })
            .catch((e) => {
                message.error(`error: ${e}`)
            })
    }

    const saveCSS = () => {
        if (localStorage.getItem("fileId") === "null")
            message.warning("You need to save file first");
        else {
            const formData = new FormData();
            formData.append('fileId', localStorage.getItem("fileId"));
            formData.append('fileCSS', CSS);
            // formData.append('userId', localStorage.getItem("instructorId"));

            console.log("Save CSS to backend", formData);
            axios.post(BASE_URL + "/saveCSS", formData)
                .then(res => {
                    message.success(`CSS saved`);
                    localStorage.setItem("saved", "true");
                    // localStorage.setItem("fileId", res.data.fileId);
                })
                .catch(() => message.error('error'));
        }
    }

    return (
        <Container>
            {/*<Title>Markdown Text</Title>*/}
            <Title>
                <TitleArea placeholder="Please type a file name"
                           rows="1" />
                <div style={{paddingTop: 1}}>
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={saveCSS}>
                        <SaveOutlined /> Save CSS
                    </Button>
                    {/*<Button size={"small"} style={{marginLeft: 10}}*/}
                    {/*        onClick={discard}>*/}
                    {/*    <DeleteOutlined /> Discard*/}
                    {/*</Button>*/}
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={() => props.setDisplay("Text")}>
                        <DeleteOutlined /> Edit Text
                    </Button>
                </div>
            </Title>
            <TextArea placeholder="Please type in MarkDown"
                      onChange={onInputChange}
                      value={CSS}/>
        </Container>
    );
}
