import React, { useContext, useRef } from "react";
import ReactDOM from 'react-dom';
import styled from "styled-components";
import editorContext from "./editorContext";
import {Button, message} from "antd";
import {DeleteOutlined, SaveOutlined} from "@ant-design/icons";
import titleContext from "./titleContext";
import axios from "axios";
import {BASE_URL} from "../../config/config";

const Container = styled.div`
  width: 50%;
  height: calc(100vh - 60px);
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

export function Input(props) {
    // const { setTitleText } = useContext(titleContext);

    var confirmDiscard = false;

    const discard = () => {

        if (localStorage.getItem("saved") === "true") {
            window.alert("You have not make any changes.")
        } else {
            confirmDiscard = window.confirm("Are you sure to discard all the changes?");
            if (confirmDiscard === true) {
                localStorage.setItem("fileId", "null");
                localStorage.setItem("newFileName", "");
                localStorage.setItem("newFileString", "");
                localStorage.setItem("saved", "true");
            }
        }
    }

    const onTitleChange = e => {
        const newValue = e.currentTarget.value;
        localStorage.setItem("newFileName", newValue);
        localStorage.setItem("saved", "false");
        // setTitleText(newValue);
    };

    const { setMarkdownText } = useContext(editorContext);
    const onInputChange = e => {
        if (confirmDiscard === true) {
            e.currentTarget.value = "";
            confirmDiscard = false;
        }

        const newValue = e.currentTarget.value;
        localStorage.setItem("newFileString", newValue);
        localStorage.setItem("saved", "false");
        setMarkdownText(newValue);

    }

    const saveFile =()=>{
        const formData = new FormData();
        formData.append('fileId', localStorage.getItem("fileId"));
        formData.append('fileName', localStorage.getItem("newFileName"));
        formData.append('rawString', localStorage.getItem("newFileString"));
        formData.append('userId', localStorage.getItem("instructorId"));

        console.log("Save file to backend", formData);
        axios.post(BASE_URL + "/save", formData)
            .then(res => {message.success(`File saved`);
                localStorage.setItem("saved", "true");
                localStorage.setItem("fileId", res.data.fileId);
                })
            .catch(() => message.error('error'));

        // onInputChange;
        // onTitleChange;
    }

    return (
        <Container>
            {/*<Title>Markdown Text</Title>*/}
            <Title>
                <TitleArea placeholder="Please type a file name"
                           rows="1"
                           onChange={onTitleChange}>
                    {localStorage.getItem("newFileName")}
                </TitleArea>
                <div style={{paddingTop: 1}}>
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={saveFile}>
                        <SaveOutlined /> Save File
                    </Button>
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={discard}>
                        <DeleteOutlined /> Discard
                    </Button>
                </div>
            </Title>
            <TextArea placeholder="Please type in MarkDown"
                      onChange={onInputChange}>
                {localStorage.getItem("newFileString")}
            </TextArea>
        </Container>
    );
}
