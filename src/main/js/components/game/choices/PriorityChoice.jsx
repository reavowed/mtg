import _ from "lodash";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../../contexts/ActionManager";
import DecisionMaker from "../../../contexts/DecisionMaker";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import DecisionButton from "../DecisionButton";
import $ from "jQuery";

function ActionText({text}) {
    const elements = [];
    while (text !== "") {
        if (text.startsWith("{")) {
            const closingBraceIndex = text.indexOf("}")
            const symbolContents = text.substring(1, closingBraceIndex);
            elements.push(<span className={"card-symbol card-symbol-" + symbolContents}/>);
            text = text.substring(closingBraceIndex + 1);
        } else {
            const openingBraceIndex = text.indexOf("{");
            if (openingBraceIndex > -1) {
                elements.push(text.substring(0, openingBraceIndex))
                text = text.substring(openingBraceIndex);
            } else {
                elements.push(text);
                text = "";
            }
        }
    }
    return elements;
}

function ActionMenu({actions, event}) {
    const decisionMaker = useContext(DecisionMaker);
    return <div class="dropdown-menu dropdown-menu-sm" id="context-menu" style={{display: "block", position: "fixed", top: event.pageY, left: event.pageX }} >
        {actions.map(action => <a class="dropdown-item" href="#" key={action.optionText} onclick={() => decisionMaker.makeDecision(action.optionText)}>
            <ActionText text={action.displayText} />
        </a>)}
    </div>
}

export default function PriorityChoice() {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);
    const [actionDetails, setActionDetails] = useState(null);
    const onObjectClick = useCallback((objectId, event) => {
        event.originalEvent.fromHandler = this;
        const actions = _.filter(gameState.currentChoice.details.availableActions, action => action.objectId === objectId);
        setActionDetails({actions, event});
    }, [gameState]);
    const onDocumentClick = useCallback((event) => {
        if (actionDetails && event.originalEvent.fromHandler !== this) setActionDetails(null);
    }, [actionDetails]);
    useEffect(() => {
        actionManager.setActionHandler(() => onObjectClick);
        return () => actionManager.setActionHandler(null);
    }, [onObjectClick]);
    useEffect(() => {
        $(document).on("click", onDocumentClick);
        return () => $(document).off("click", onDocumentClick);
    }, [onDocumentClick]);
    return <div>
        <BannerText as="p">Cast spells, activate abilities, or play a land.</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose="Pass">Pass</DecisionButton>
        </HorizontalCenter>
        {actionDetails && actionDetails.actions.length > 0 && <ActionMenu {...actionDetails} /> }
    </div>;
}
