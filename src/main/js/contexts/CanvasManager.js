import $ from "jquery";
import _ from "lodash";
import {createContext} from "preact";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import {useInterval} from "../utils/hook-utils";
import ObjectRefManager from "./ObjectRefManager";

const CanvasManager = createContext(null);
const InternalProvider = CanvasManager.Provider;

CanvasManager.Provider = function({children}) {
    const objectRefManager = useContext(ObjectRefManager);
    const [canvasRef, setCanvasRef] = useState(null);
    const [temporaryLineStart, setTemporaryLineStart] = useState(null);
    const [temporaryLineEnd, setTemporaryLineEnd] = useState(null);
    const [lines, setLines] = useState([]);

    // Reset temp line end when line start set
    useEffect(() => {
        if (temporaryLineStart) {
            setTemporaryLineEnd({
                x: temporaryLineStart.event.pageX,
                y: temporaryLineStart.event.pageY
            });
        } else {
            setTemporaryLineEnd(null);
        }
    }, [temporaryLineStart]);

    const drawLines = useCallback(() => {
        function getMidpoint(ref) {
            const rect = ref.getBoundingClientRect();
            return {
                x: rect.left + (rect.width/2),
                y: rect.top + (rect.height/2),
            }
        }

        if (canvasRef) {
            canvasRef.width = $(document).width();
            canvasRef.height = $(document).height();
            const context = canvasRef.getContext('2d');
            context.clearRect(0, 0, canvasRef.width, canvasRef.height);
            context.strokeStyle = 'dodgerblue';
            context.lineWidth = 5;

            if (temporaryLineStart && temporaryLineEnd) {
                const startRef = objectRefManager.getObjectRef(temporaryLineStart.objectId);
                if (startRef) {
                    const {x: startX, y: startY} = getMidpoint(startRef);
                    context.beginPath();
                    context.moveTo(startX, startY);
                    context.lineTo(temporaryLineEnd.x, temporaryLineEnd.y);
                    context.stroke();
                }
            }

            _.forEach(lines, ([lineStart, lineEnd]) => {
                const startRef = objectRefManager.getObjectRef(lineStart);
                const endRef = objectRefManager.getObjectRef(lineEnd);
                if (startRef && endRef) {
                    const {x: startX, y: startY} = getMidpoint(startRef);
                    const {x: endX, y: endY} = getMidpoint(endRef);
                    context.beginPath();
                    context.moveTo(startX, startY);
                    context.lineTo(endX, endY);
                    context.stroke();
                }
            })
        }
    }, [canvasRef, temporaryLineStart, temporaryLineEnd, lines, objectRefManager]);

    // Redraw on any change
    useEffect(drawLines, [drawLines]);
    useInterval(drawLines, 100);

    // Update temp line end on mouse move
    const moveMouseOnDocumentHandler = useCallback(event => {
        if (temporaryLineStart) {
            setTemporaryLineEnd({x: event.pageX, y: event.pageY});
        }
    }, [temporaryLineStart]);
    useEffect(() => {
        $(document).on("mousemove", moveMouseOnDocumentHandler);
        return () => $(document).off("mousemove", moveMouseOnDocumentHandler);
    }, [moveMouseOnDocumentHandler]);

    return <InternalProvider value={{setTemporaryLineStart, setLines}}>
        {children}
        <canvas ref={setCanvasRef} className="overlay" style={{position: "absolute", top: 0, left: 0}}/>
    </InternalProvider>;
}

export default CanvasManager;
