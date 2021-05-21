import {Fragment} from "preact";
import {useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../ActionManager";
import DecisionMaker from "../../DecisionMaker";
import ScryfallService from "../../ScryfallService";
import $ from "jQuery";
import {addClass} from "../../utils/element-utils";
import {useRefWithEventHandler} from "../../utils/hook-utils";

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

function ActionMenu({actions, ...props}) {
    const decisionMaker = useContext(DecisionMaker);
    return <div class="dropdown-menu dropdown-menu-sm" id="context-menu" {...props} >
        {actions.map(action => <a class="dropdown-item" href="#" key={action.option} onclick={() => decisionMaker.makeDecision(action.option)}>
            <ActionText text={action.text} />
        </a>)}
    </div>
}

export default function Card({card, ...props}) {
    const scryfallService = useContext(ScryfallService);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);

    const actionManager = useContext(ActionManager);
    const actions = actionManager.getActions(card.objectId);
    const [actionMenuDetails, setActionMenuDetails] = useState(null);
    function handleClick(event) {
        const details = {
            left: event.pageX,
            top: event.pageY
        }
        setActionMenuDetails(details);
        event.originalEvent.fromHandler = handleClick;
        $(document).on("click", handleDocumentClick);
    }
    function handleDocumentClick(event) {
        if (event.originalEvent.fromHandler !== handleClick) setActionMenuDetails(null);
    }
    function addImageHandler(img) {
        actions && actions.length > 0 && $(img).on("click", handleClick);
    }
    function removeImageHandler(img) {
        $(img).off("click", handleClick);
        $(document).off("click", handleDocumentClick);
    }

    const imgRef = useRefWithEventHandler(addImageHandler, removeImageHandler, [actions]);

    const containerClasses = ["cardContainer"]
    if (card.permanentStatus && card.permanentStatus.isTapped) {
        containerClasses.push("tapped")
    }

    return scryfallCard && <div className={containerClasses.join(" ")}>
        <img ref={imgRef} src={scryfallCard.image_uris.small} {...props} />
        {actionMenuDetails && <ActionMenu actions={actions} style={{display: "block", top: actionMenuDetails.top, left: actionMenuDetails.left }} />}
    </div>
}
