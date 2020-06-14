/**
 * The ResultPresenter component is to show instructor the result chart of quiz
 */
import React from 'react';
import { CSSTransitionGroup } from 'react-transition-group';
import QuizStatictic from './quiz_components/QuizStatistic'
import { Button } from 'antd';

/**
 * The function ResultPresenter is to render statistic data and back button
 * @param props
 * @returns {*}
 * @constructor
 */
function ResultPresenter(props) {
    return (
        <div>
            <CSSTransitionGroup
                className="container result"
                component="div"
                transitionName="fade"
                transitionEnterTimeout={800}
                transitionLeaveTimeout={500}
                transitionAppear
                transitionAppearTimeout={500}
            >
            <div>
                Here are the quiz statistics:
            </div>
            </CSSTransitionGroup>
            <QuizStatictic fileId = {props.fileId}/>
            <Button onClick={props.toSlidesCallback}>
                Back to Slides
            </Button>
        </div>
    );
}

export default ResultPresenter;
