import {Fragment} from "preact";
import {useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../ActionManager";
import DecisionMaker from "../../DecisionMaker";
import ScryfallService from "../../ScryfallService";
import $ from "jQuery";
import {useRefWithEventHandler} from "../../utils/hook-utils";

function ActionMenu({actions, ...props}) {
    const decisionMaker = useContext(DecisionMaker);
    return <div class="dropdown-menu dropdown-menu-sm" id="context-menu" {...props} >
        {actions.map(action => <a class="dropdown-item" href="#" key={action.option} onclick={() => decisionMaker.makeDecision(action.option)}>{action.text}</a>)}
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

    return scryfallCard && <Fragment>
        <img ref={imgRef} src={scryfallCard.image_uris.small} {...props} />
        {actionMenuDetails && <ActionMenu actions={actions} style={{display: "block", top: actionMenuDetails.top, left: actionMenuDetails.left }} />}
    </Fragment>
}
