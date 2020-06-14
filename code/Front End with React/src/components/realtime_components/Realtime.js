import React, { useState } from "react";
// import "../styles.css";
import styled from "styled-components";
import { Input } from "./Input";
import { Render } from "./Render";
import EditorContext from "./editorContext";

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
    const [markdownText, setMarkdownText] = useState("");
    // need to be initialize separately
    const [titleText, setTitleText] = useState("");

    const contextValue = {
        titleText,
        setTitleText,
        markdownText,
        setMarkdownText
    };

    return (
        <EditorContext.Provider value={contextValue}>
            <AppContainer>
                <Title>Markdown Editor</Title>
                <EditorContainer>
                    <Input />
                    <Render />
                </EditorContainer>
            </AppContainer>
        </EditorContext.Provider>
    );
}
