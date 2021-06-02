import {forwardRef} from "preact/compat";
import CardImage from "./CardImage";

export default forwardRef(function CardWithText({card, children, ...props}, ref) {
    return <CardImage ref={ref} card={card} {...props}>
        <div className="cardText">{card.text}</div>
        {children}
    </CardImage>;
});
