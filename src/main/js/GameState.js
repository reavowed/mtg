import {createContext} from "preact";
import {useEffect, useState} from "preact/hooks";
import ScreenCenter from "./components/layout/ScreenCenter";
import {getCurrentPath, getJson} from "./utils/fetch-helpers";
import SockJS from 'sockjs-client';
import {Stomp} from 'stompjs/lib/stomp';
import 'net';

const GameState = createContext(null);
const InternalProvider = GameState.Provider;
GameState.Provider = function({children}) {
    const [state, setState] = useState(null);
    useEffect(() => {
        getJson('$currentPath/state').then(setState)
        const socket = new SockJS('/state');
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/user/' + getCurrentPath().split('/')[1] + '/topic/state', function(messageOutput) {
                setState(JSON.parse(messageOutput.body));
            });
        });
    }, []);
    return <InternalProvider value={state}>{state ? children : <ScreenCenter><div className="spinner-border"/></ScreenCenter> }</InternalProvider>;
}

export default GameState;
