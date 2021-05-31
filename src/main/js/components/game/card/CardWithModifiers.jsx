import _ from "lodash";
import {forwardRef} from "preact/compat";
import CardImage from "./CardImage";

export default forwardRef(function CardWithModifiers({card, containerClasses, ...props}, ref) {
    containerClasses = containerClasses || [];
    if (card.permanentStatus && card.permanentStatus.isTapped) {
        containerClasses.push("tapped")
    }
    if (_.has(card.modifiers, "attacking")) {
        containerClasses.push("attacking");
    }
    if (_.has(card.modifiers, "blocking")) {
        containerClasses.push("blocking");
    }

    return <CardImage ref={ref} card={card} className={containerClasses.join(" ")}>
            {card.markedDamage > 0 && <span className="position-absolute damage">{card.markedDamage}</span>}
        </CardImage>;
});
