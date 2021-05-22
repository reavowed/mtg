import {forwardRef} from "preact/compat";
import {addClass} from "../../utils/element-utils";

export default forwardRef(function VerticalCenter({className, ...props}, ref) {
    return <div ref={ref} className={addClass(className, "d-flex flex-column justify-content-center h-100")} {...props}/>;
})
