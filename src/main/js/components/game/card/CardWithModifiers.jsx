import _ from "lodash";
import {forwardRef} from "preact/compat";
import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../../contexts/ScryfallService";
import CardImage from "./CardImage";

export default forwardRef(function CardWithModifiers({card, containerClasses, ...props}, ref) {
    containerClasses = ["cardContainer", "position-relative", ...(containerClasses || [])];
    if (card.permanentStatus && card.permanentStatus.isTapped) {
        containerClasses.push("tapped")
    }
    if (_.has(card.modifiers, "attacking")) {
        containerClasses.push("attacking");
    }
    if (_.has(card.modifiers, "blocking")) {
        containerClasses.push("blocking");
    }

    return <div className={containerClasses.join(" ")}>
        <CardImage ref={ref} card={card} />
        {card.markedDamage > 0 && <span className="position-absolute damage">{card.markedDamage}</span>}
    </div>;
});
