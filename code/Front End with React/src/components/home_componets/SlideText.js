import React from "react";
import {Text} from "react-konva";

class SlideText extends React.Component {
    constructor(args) {
        super(args);
        this.state = {
            title: args.title,
            startX: args.x,
        }
    };
    render() {
        return (
            <Text
                text={this.state.title}
                x={this.state.startX}
                y={10}
                align={'center'}
                fontFamily={"Calibri"}
                width={160}
                height={100}
                fontSize={18}
                offsetY={-40}
            />
        );
    }
}

export default SlideText;
