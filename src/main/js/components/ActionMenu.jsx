import $ from "jquery";
import _ from "lodash";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../contexts/ActionManager";
import DecisionMaker from "../contexts/DecisionMaker";
import {FormattedText} from "./FormattedText";

export function ActionMenu({actions, event}) {
    const decisionMaker = useContext(DecisionMaker);
    return <div class="dropdown-menu dropdown-menu-sm actionMenu" id="context-menu"
                style={{display: "block", position: "absolute", top: event.pageY, left: event.pageX}}>
        {actions.map(action => <a class="dropdown-item" href="#" key={action.optionText}
                                  onclick={() => decisionMaker.makeDecision(action.optionText)}>
            <FormattedText text={action.displayText}/>
        </a>)}
    </div>
}

export function useActionMenu(allActions) {
    const actionManager = useContext(ActionManager);
    const [actionDetails, setActionDetails] = useState(null);
    const getActions = useCallback((objectId) => {
        return _.filter(allActions, action => action.objectId === objectId);
    }, [allActions]);
    const onObjectClick = useCallback((objectId, event) => {
        event.originalEvent.fromHandler = this;
        const actions = getActions(objectId);
        setActionDetails({actions, event});
    }, [allActions]);
    const onDocumentClick = useCallback((event) => {
        if (actionDetails && event.originalEvent.fromHandler !== this) setActionDetails(null);
    }, [actionDetails]);
    const getClasses = useCallback((objectId) => {
        if (getActions(objectId).length) {
            return ["selectable"];
        } else {
            return [];
        }
    }, [getActions]);
    useEffect(() => {
        actionManager.setActionHandler(() => onObjectClick);
        actionManager.setClassGetter(() => getClasses);
        return () => {
            actionManager.setActionHandler(null);
            actionManager.setClassGetter(null);
        }
    }, [onObjectClick]);
    useEffect(() => {
        $(document).on("click", onDocumentClick);
        return () => $(document).off("click", onDocumentClick);
    }, [onDocumentClick]);
    return actionDetails && actionDetails.actions.length > 0 && <ActionMenu {...actionDetails} />;
}
