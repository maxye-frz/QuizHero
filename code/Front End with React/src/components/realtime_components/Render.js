import React, { useContext } from "react";
import styled from "styled-components";
// import ReactMarkdown from "react-markdown";
import marpitConvert from "../Marpit";
import realtimeTheme from "../default_theme/realtime-theme";
import editorContext from "./editorContext";
import separateQuestion from "../Parse";
import {Link} from "react-router-dom";
import {Button} from "antd";
import {FilePptOutlined} from "@ant-design/icons";
import marpit2realtime from "../default_theme/marpit2realtime";
import marpit2spectacle from "../default_theme/marpit2spectacle";

const Container = styled.div`
    width: calc(50vw);
    height: 100%;
    padding: 13px;
    // font-family: "Lato", sans-serif;
`;

const Title = styled.div`
    width: 100%; 
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 1em;
    padding: 8px 0;
    border-bottom: 1px solid rgba(15, 15, 15, 0.3);
`;

const ResultArea = styled.div`
    width: 100%;
    height: 88%;
    border: none;
    overflow-y: scroll;
    text-align: left;
`;


export function Render(props) {
    const { titleText, markdownText, CSS } = useContext(editorContext);

    // pre-set newFileString to "", because Marpit cannot take null input
    // const markdownText = localStorage.getItem("newFileString") ? localStorage.getItem("newFileString") : ""

    console.log(editorContext);

    const callSeparateQuestion =(rawString)=>{
        var data = separateQuestion(rawString);
        data.CSS = marpit2spectacle(CSS);
        data = JSON.stringify(data);
        localStorage.setItem("data", data)
        // this.jump();
    }

    return (
        <Container>
            <Title>
                {titleText}
                <Link to={{pathname: '/presenter'}} target = '_blank'>
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={() => callSeparateQuestion(markdownText)}>
                        <FilePptOutlined /> Preview Presentation
                    </Button>
                </Link>
            </Title>
            <ResultArea>
                {/*<ReactMarkdown source={markdownText} />*/}
                <div dangerouslySetInnerHTML = {{__html: marpitConvert(markdownText, marpit2realtime(CSS))}}/>
            </ResultArea>
        </Container>
    );
}