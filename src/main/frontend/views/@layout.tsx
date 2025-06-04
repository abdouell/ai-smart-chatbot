import "bootstrap/dist/css/bootstrap.min.css"
import {NavLink, Outlet} from "react-router-dom";

export default function Layout() {
    return (
        <div className="p-m">
            <nav>
                <NavLink to="/" className="btn btn-outline-info m-1">Home</NavLink>
                <NavLink to="/chat" className="btn btn-outline-info m-1">Chat</NavLink>
                <NavLink to="/person" className="btn btn-outline-info m-1">Person</NavLink>
            </nav>
            <main>
                <Outlet></Outlet>
            </main>
        </div>
    );
}
