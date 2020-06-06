import React, { useContext } from "react";
import styled from "styled-components";
import ReactMarkdown from "react-markdown";
import marpitConvert from "../Marpit";
import editorContext from "./editorContext";
import titleContext from "./titleContext";
import separateQuestion from "../Parse";
import {Link} from "react-router-dom";
import {Button} from "antd";

const Container = styled.div`
  width: 50%;
  height: 100%;
  padding: 13px;
  // font-family: "Lato", sans-serif;
`;

const Title = styled.div`
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 1em;
  padding: 8px 0;
  border-bottom: 1px solid rgba(15, 15, 15, 0.3);
`;

const ResultArea = styled.div`
  width: 100%;
  height: 100%;
  border: none;
  // font-size: 17px;
`;

export function Result(props) {
    const { markdownText } = useContext(editorContext);
    const { titleText } = useContext(titleContext);

    const callSeparateQuestion =(rawString)=>{
        var data = separateQuestion(rawString);
        data = JSON.stringify(data);
        localStorage.setItem("data", data)
        // this.jump();
    }

    return (
        <Container>
            <Title>
                Converted Text
                <Link to={{pathname: '/presenter'}} target = '_blank'>
                    <Button size={"small"} style={{marginLeft: 10}}
                            onClick={callSeparateQuestion(markdownText)}>
                        Preview Presentation
                    </Button>
                </Link>
            </Title>
            <ResultArea>
                {/*<ReactMarkdown source={markdownText} />*/}
                <div dangerouslySetInnerHTML={{__html: marpitConvert(markdownText)}}></div>
            </ResultArea>
        </Container>
    );
}
