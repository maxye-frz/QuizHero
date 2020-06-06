import React, { useState } from "react";
// import "../styles.css";
import styled from "styled-components";
import { Input } from "./realtime_components/Input";
import { Render } from "./realtime_components/Render";
import EditorContext from "./realtime_components/editorContext";

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
  height: 100%;
  display: flex;
`;

export default function App() {
    const [markdownText, setMarkdownText] = useState("");

    const contextValue = {
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
