import {forwardRef} from "preact/compat";
import CardImage from "./CardImage";

export default forwardRef(function CardWithText(props, ref) {
    return <CardImage ref={ref} showText={true} {...props} />;
});
