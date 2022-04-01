import {forwardRef} from "preact/compat";
import {addClass} from "../../utils/element-utils";

export default forwardRef(function CardBack({className, ...props}, ref) {
    return <img ref={ref} className={addClass(className, "cardBack")} src="https://c1.scryfall.com/file/scryfall-card-backs/small/0a/0aeebaf5-8c7d-4636-9e82-8c27447861f7.jpg" {...props} />
});
