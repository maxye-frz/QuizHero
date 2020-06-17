import React from "react";
import {Rect} from "react-konva";

class SlideBackground extends React.Component {
    constructor(args) {
        super(args);
        this.state = {
            startX: args.x
        }
    };
    render() {
        return (
            <Rect
                x={this.state.startX}
                y={10}
                width={160}
                height={100}
                fill={'rgba(217, 218, 217, 0.1)'}
                shadowBlur={10}
                lineCap={"round"}
                lineJoin={"round"}
            />
        );
    }
}

export default SlideBackground;
