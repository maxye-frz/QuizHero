import React from "react";
import {Rect} from "react-konva";

class ViewButton extends React.Component {
    constructor(props) {
        super(props);
        const image = new window.Image();
        image.onload = () => {
            this.setState({
                fillImage: image
            });
        }
        image.src = require('../../fig/view-icon.png');
        this.state = {
            fillImage: null
        }
    }

    render() {
        return (
            <Rect
                x={this.props.x}
                y={this.props.y}
                width={this.props.width}
                height={this.props.height}
                onClick={this.props.present}
                opacity={0.2}
                shadowBlur={2.5}
                lineCap={"round"}
                lineJoin={"round"}
                fillPatternImage={this.state.fillImage}
            />
        );
    }
}

export default ViewButton;