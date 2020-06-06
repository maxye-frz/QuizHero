import React, { useContext } from "react";
import styled from "styled-components";
import editorContext from "./editorContext";
import {Button} from "antd";
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
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 1em;
  padding: 8px 0;
  border-bottom: 1px solid rgba(15, 15, 15, 0.3);
`;

const TextArea = styled.textarea`
  width: 100%;
  height: 100%;
  resize: none;
  border: none;
  outline: none;
  font-size: 17px;
`;

const TitleDiv = styled.div`
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 1em;
  border-bottom: 1px solid rgba(15, 15, 15, 0.3);
`;

const TitleArea = styled.textarea`
  width: 25%;
  height: 100%;
  align-items: center;
  justify-content: center;
  resize: none;
  // border: none;
  outline: none;
  font-size: 17px;
`;

export function MarkedInput(props) {
    const { setTitleText } = useContext(titleContext);

    const onTitleChange = e => {
        const newValue = e.currentTarget.value;
        setTitleText(newValue);
    };

    const { setMarkdownText } = useContext(editorContext);

    const onInputChange = e => {
        const newValue = e.currentTarget.value;
        setMarkdownText(newValue);
    };

    const saveFile =(fileName, rawString)=>{
        const formData = new FormData();
        formData.append('fileName', fileName);
        formData.append('rawString', rawString);
        formData.append('userId', localStorage.getItem("instructorId"));
        console.log("Save file to backend", formData);
        axios.post(BASE_URL + "/save", formData)
            .then(res => {
                console.log("CCC",res.data);
                // this.setState({fileId : res.data.fileId})
                // resolve(res.data.fileId);
                alert("File uploaded successfully.");
            })
            .catch((error) => {
                // reject(error);
                // alert("File uploaded failed.");
            });
    }

    return (
        <Container>
            {/*<Title>Markdown Text</Title>*/}
            <TitleDiv>
                <TitleArea onChange={{onTitleChange}}/>
                <Button size={"small"} style={{marginLeft: 10}}
                        onClick={saveFile(titleContext, editorContext)}>
                    Save
                </Button>
            </TitleDiv>
            <TextArea onChange={onInputChange} />
        </Container>
    );
}
