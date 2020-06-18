import React from "react";
import {Text} from "react-konva";

class SlideText extends React.Component {
    constructor(props) {
        super(props);
    };
    render() {
        return (
            <Text
                text={this.props.title}
                x={this.props.x}
                y={this.props.y}
                align={'center'}
                fontFamily={"Calibri"}
                width={this.props.width}
                height={this.props.height}
                fontSize={18}
                offsetY={this.props.offsetY}
            />
        );
    }
}

export default SlideText;
