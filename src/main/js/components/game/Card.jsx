import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../contexts/ActionManager";
import ScryfallService from "../../contexts/ScryfallService";
import $ from "jQuery";
import {useRefWithEventHandler} from "../../utils/hook-utils";

export default function Card({card, ...props}) {
    const scryfallService = useContext(ScryfallService);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);

    const actionManager = useContext(ActionManager);
    const handleClick = useCallback((event) => {
        actionManager.actionHandler(card.objectId, event);
    }, [card, actionManager]);

    function addImageHandler(img) {
        $(img).on("click", handleClick);
    }
    function removeImageHandler(img) {
        $(img).off("click", handleClick);
    }

    const imgRef = useRefWithEventHandler(addImageHandler, removeImageHandler, [handleClick]);

    const containerClasses = ["cardContainer"]
    if (card.permanentStatus && card.permanentStatus.isTapped) {
        containerClasses.push("tapped")
    }

    return scryfallCard && <div className={containerClasses.join(" ")}>
        <img ref={imgRef} src={scryfallCard.image_uris.small} {...props} />
    </div>
}
