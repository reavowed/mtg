import $ from "jQuery";
import _ from "lodash";
import {useCallback, useContext} from "preact/hooks";
import ActionManager from "../../../contexts/ActionManager";
import ObjectRefManager from "../../../contexts/ObjectRefManager";
import {useRefWithEventHandler} from "../../../utils/hook-utils";
import CardWithModifiers from "./CardWithModifiers";

export default function CardWithActions({card, ...props}) {
    const objectRefManager = useContext(ObjectRefManager);

    const actionManager = useContext(ActionManager);
    const handleClick = useCallback((event) => {
        actionManager.actionHandler(card.objectId, event);
    }, [card, actionManager]);

    const registerRef = useCallback((img) => {
        $(img).on("click", handleClick);
        objectRefManager.setObjectRef(card.objectId, img);
    }, [handleClick, card]);
    const deregisterRef = useCallback((img) => {
        $(img).off("click", handleClick);
        objectRefManager.setObjectRef(card.objectId, null);
    }, [handleClick, card]);

    const imgRef = useRefWithEventHandler(registerRef, deregisterRef, []);

    return <CardWithModifiers card={card} containerClasses={actionManager.getClasses(card.objectId)} ref={imgRef} {...props}/>;
}
