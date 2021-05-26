import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../contexts/ActionManager";
import ObjectRefManager from "../../contexts/ObjectRefManager";
import ScryfallService from "../../contexts/ScryfallService";
import $ from "jQuery";
import _ from "lodash";
import {useRefWithEventHandler} from "../../utils/hook-utils";

export default function Card({card, ...props}) {
    const scryfallService = useContext(ScryfallService);
    const objectRefManager = useContext(ObjectRefManager);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);

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

    const containerClasses = ["cardContainer", "position-relative", ...actionManager.getClasses(card.objectId)];
    if (card.permanentStatus && card.permanentStatus.isTapped) {
        containerClasses.push("tapped")
    }
    if (_.has(card.modifiers, "attacking")) {
        containerClasses.push("attacking");
    }
    if (_.has(card.modifiers, "blocking")) {
        containerClasses.push("blocking");
    }

    return scryfallCard && <div className={containerClasses.join(" ")}>
        <img ref={imgRef} src={scryfallCard.image_uris.small} {...props} />
    </div>
}
