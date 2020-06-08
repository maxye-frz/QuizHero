import React, { useContext } from "react";
import styled from "styled-components";
import editorContext from "./editorContext";
import {Button, message} from "antd";
import titleContext from "./titleContext";
import axios from "axios";
import {BASE_URL} from "../../config/config";

const Container = styled.div`
  width: 50%;
  height: 100vh;
  padding: 13px;
  border-right: 1.5px solid rgba(15, 15, 15, 0.4);
  font-family: "Lato", sans-serif;
`;

const Title = styled.div`
  height: 60px;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 1em;
  padding: 2.5px 0;
  border-bottom: 1px solid rgba(15, 15, 15, 0.3);
`;

const TitleArea = styled.textarea`
  width: 60%;
  height: 100%;
  font-size: 22px;
  font-weight: 600;
  resize: none;
  border: none;
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

    const onTitleChange = e => {
        const newValue = e.currentTarget.value;
        localStorage.setItem("newFileName", newValue);
        // setTitleText(newValue);
    };

    const { setMarkdownText } = useContext(editorContext);

    const onInputChange = e => {
        const newValue = e.currentTarget.value;
        localStorage.setItem("newFileString", newValue);
        setMarkdownText(newValue);
    };

    const saveFile =()=>{
        const formData = new FormData();
        formData.append('fileId', localStorage.getItem("fileId"));
        formData.append('fileName', localStorage.getItem("newFileName"));
        formData.append('rawString', localStorage.getItem("newFileString"));
        formData.append('userId', localStorage.getItem("instructorId"));

        console.log("Save file to backend", formData);
        axios.post(BASE_URL + "/save", formData)
            .then(res => {message.success(`File saved`);
                localStorage.setItem("fileId", res.data.fileId);
                localStorage.setItem("newFileName", "");
                localStorage.setItem("newFileString", "");})
            .catch(() => message.error('error'));

        // onInputChange;
        // onTitleChange;
        return (
            <Container>
                {/*<Title>Markdown Text</Title>*/}
                <Title>
                    <TitleArea placeholder="Please type a file name"
                               rows="1"
                               onChange={onTitleChange}>
                        {localStorage.getItem("newFileName")}
                    </TitleArea>
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={saveFile}>
                        Save File
                    </Button>
                </Title>
                <TextArea placeholder="Please type in MarkDown"
                          onChange={onInputChange}>
                    {localStorage.getItem("newFileString")}
                </TextArea>
            </Container>
        );
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
                <Button size={"small"} style={{marginLeft: 10}}
                        onClick={saveFile}>
                    Save File
                </Button>
            </Title>
            <TextArea placeholder="Please type in MarkDown"
                      onChange={onInputChange}>
                {localStorage.getItem("newFileString")}
            </TextArea>
        </Container>
    );
}
