import React from "react";
import {Circle, Label, Tag, Text, Group} from "react-konva";
import styled from "styled-components";

class SlideButton extends React.Component {
    constructor(props) {
        super(props);

    };

    handleClick = () => {
        this.render = () => {
            return (
                <Group onMouseLeave={this.handleMouseLeave}>
                <Label x={this.props.x} y={this.props.y}
                       onClick={this.handleClick}
                >
                    <Tag
                        fill={'grey'}
                        lineJoin={'round'}
                        shadowColor={'grey'}
                        shadowBlur={5}
                        shadowOffset={5}
                        shadowOpacity={0.75}
                        cornerRadius={5}
                    />
                    <Text
                        text={'Delete File'}
                        fontFamily={'Calibri'}
                        fontSize={12}
                        padding={2.5}
                        fill={'white'}
                        width={this.props.width}
                        onClick={this.props.delete}
                        height={20}
                        align={'center'}
                    />
                </Label>

                <Label x={this.props.x} y={this.props.y + 20}
                           onClick={this.handleClick}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                        />
                        <Text
                            text={'Download Raw'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.downloadRaw}
                            height={20}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 40}
                           onClick={this.handleClick}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                        />
                        <Text
                            text={'Download HTML'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.downloadHTML}
                            height={20}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 60}
                           onClick={this.handleClick}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                        />
                        <Text
                            text={'Present'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.present}
                            height={20}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 60}
                           onClick={this.handleClick}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                        />
                        <Text
                            text={'Start Sharing'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.share}
                            height={20}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 80}
                           onClick={this.handleClick}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                        />
                        <Text
                            text={'Stop Sharing'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.stopShare}
                            align={'center'}
                            height={20}
                        />
                </Label>
            </Group>
            );
        }
    }

    handleMouseLeave = () => {
        this.render = () => {
            return (
                <Label x={this.props.x + 40} y={this.props.y}
                       onClick={this.handleClick}
                       // onMouseLeave={this.handleMouseLeave}
                >
                    <Tag
                        fill={'grey'}
                        lineJoin={'round'}
                        shadowColor={'black'}
                        shadowBlur={10}
                        shadowOffset={10}
                        shadowOpacity={0.75}
                        cornerRadius={2}
                    />
                    <Text
                        text={'More'}
                        fontFamily={'Calibri'}
                        fontSize={12}
                        padding={5}
                        fill={'white'}
                        width={this.props.width - 40}
                        align={'center'}
                    />
                </Label>
            );
        }
    }

    render() {
        return (
            <Label x={this.props.x + 40} y={this.props.y}
                   onClick={this.handleClick}
                   // onMouseLeave={this.handleMouseLeave}
            >

                <Tag
                    fill={'grey'}
                    lineJoin={'round'}
                    shadowColor={'black'}
                    shadowBlur={10}
                    shadowOffset={10}
                    shadowOpacity={0.75}
                    cornerRadius={2}
                />
                <Text
                    text={'More'}
                    fontFamily={'Calibri'}
                    fontSize={12}
                    padding={5}
                    fill={'white'}
                    width={this.props.width - 40}
                    align={'center'}
                />
            </Label>
        );
    }
}


export default SlideButton;
