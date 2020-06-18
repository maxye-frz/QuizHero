import React from "react";
import {Rect} from "react-konva";

class SlideBackground extends React.Component {
    constructor(props) {
        super(props);
        const image = new window.Image();
        image.onload = () => {
            this.setState({
                fillImage: image
            });
        }
        image.src = require('../../fig/background.png');
        this.state = {
            fillImage: null
        }
    };

    render() {
        // console.log(this.state.width)
        return (
            <Rect
                x={this.props.x}
                y={this.props.y}
                width={this.props.width}
                height={this.props.height}
                // fill={'rgba(217, 218, 217, 0.1)'}
                shadowBlur={10}
                lineCap={"round"}
                lineJoin={"round"}
                fillPatternImage={this.state.fillImage}
            />
        );
    }
}

export default SlideBackground;
