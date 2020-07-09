import React, { useState } from "react";
// import "../styles.css";
import styled from "styled-components";
import { Input } from "./Input";
import { Render } from "./Render";
import editorContext from "./editorContext";
import {InputCSS} from "./InputCSS";

const AppContainer = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const Title = styled.div`
  font-size: 25px;
  font-weight: 700;
  font-family: "Lato", sans-serif;
  margin-bottom: 1em;
`;

const EditorContainer = styled.div`
  width: 100%;
  height: calc(100vh - 60px);
  display: flex;
`;

export default function Editor() {
    const [titleText, setTitleText] = useState("");
    const [markdownText, setMarkdownText] = useState("");
    const [CSS, setCSS] = useState("")
    const [display, setDisplay] = useState("Text")
    // useState will return an array of length 2: arr = useState(""):
    // arr[0] is the state variable, arr[1] is the set function

    const contextValue = {
        titleText, // titleText: titleText
        setTitleText, // setTitleText: setTitleText
        markdownText, // markdownText: markdownText
        setMarkdownText, // setMarkdownText: setMarkdownText
        CSS,
        setCSS
    };

    return (
        <editorContext.Provider value={contextValue}>
            <AppContainer>
                <Title>Markdown Editor</Title>
                {/*Markdown Editor*/}
                <EditorContainer>
                    <div style={{display: display === "Text" ? "flex" : "none"}}>
                        <Input setDisplay={setDisplay}/>
                    </div>
                    <div style={{display: display === "CSS" ? "flex" : "none"}}>
                        <InputCSS setDisplay={setDisplay} />
                    </div>

                    <Render />
                </EditorContainer>
            </AppContainer>
        </editorContext.Provider>
    );
}
