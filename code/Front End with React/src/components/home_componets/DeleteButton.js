import React from "react";
import {Rect} from "react-konva";

class DeleteButton extends React.Component {
    constructor(props) {
        super(props);
        const image = new window.Image();
        image.onload = () => {
            this.setState({
                fillImage: image
            });
        }

        // image.style.width = '25px';
        // image.style.flex = '1';
        // image.style.height = '25px';
        image.src = require('../../fig/trash-icon.png');
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
                onClick={this.props.delete}
                opacity={0.5}
                shadowBlur={2.5}
                lineCap={"round"}
                lineJoin={"round"}
                fillPatternImage={this.state.fillImage}
            />
        );
    }
}

export default DeleteButton;