import {forwardRef} from "preact/compat";
import {addClass} from "../../utils/element-utils";

export default forwardRef(function HorizontalCenter({className, ...props}, ref) {
    return <div ref={ref} className={addClass(className, "d-flex justify-content-center w-100")} {...props} />
});
