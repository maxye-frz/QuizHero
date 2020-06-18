import React from "react";
import {Circle, Label, Tag, Text, Group} from "react-konva";

class SlideButton extends React.Component {
    constructor(props) {
        super(props);

    };

    handleClick = () => {
        this.render = () => {
            return ( <Group onMouseLeave={this.handleMouseLeave}>
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
                        // pointerHeight={50}
                    />
                    <Text
                        text={'Delete'}
                        fontFamily={'Calibri'}
                        fontSize={12}
                        padding={2.5}
                        fill={'white'}
                        width={this.props.width}
                        onClick={this.props.delete}
                        height={15}
                        align={'center'}
                    />
                </Label>

                <Label x={this.props.x} y={this.props.y + 15}
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
                            // pointerHeight={50}
                        />
                        <Text
                            text={'Edit'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.edit}
                            height={15}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 30}
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
                            // pointerHeight={50}
                        />
                        <Text
                            text={'Download'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.download}
                            height={15}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 45}
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
                            // pointerHeight={50}
                        />
                        <Text
                            text={'Present'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.present}
                            height={15}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 60}
                           onClick={this.handleClick}
                           // onMouseLeave={this.handleMouseLeave}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                            // pointerHeight={50}
                        />
                        <Text
                            text={'Share'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.share}
                            height={15}
                            align={'center'}
                        />
                </Label>
                <Label x={this.props.x} y={this.props.y + 75}
                           onClick={this.handleClick}
                           // onMouseLeave={this.handleMouseLeave}
                    >
                        <Tag
                            fill={'grey'}
                            lineJoin={'round'}
                            shadowColor={'black'}
                            shadowBlur={5}
                            shadowOffset={5}
                            shadowOpacity={0.75}
                            cornerRadius={5}
                            // pointerHeight={50}
                        />
                        <Text
                            text={'Stop Sharing'}
                            fontFamily={'Calibri'}
                            fontSize={12}
                            padding={2.5}
                            fill={'white'}
                            width={this.props.width}
                            onClick={this.props.stopshare}
                            align={'center'}
                            height={30}
                        />
                </Label>
            </Group>
            );
        }

    }
    handleMouseLeave = () => {
        this.render = () => {
            return (
                <Label x={this.props.x} y={this.props.y}
                       onClick={this.handleClick}
                       onMouseLeave={this.handleMouseLeave}
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
                        width={this.props.width}
                        align={'center'}
                    />
                </Label>
            );
        }
    }
    render() {
        return (
            <Label x={this.props.x} y={this.props.y}
                   onClick={this.handleClick}
                   onMouseLeave={this.handleMouseLeave}
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
                    width={this.props.width}
                    align={'center'}
                />
            </Label>
        );
    }
}


export default SlideButton;
