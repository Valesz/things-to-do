import Navbar from './components/shared/navbar'
import {Outlet} from 'react-router-dom'
import {CookiesProvider} from 'react-cookie'

function App() {
    return (
        <>
            <Navbar />
            <CookiesProvider>
                <Outlet />
            </CookiesProvider>
        </>
    );
}

export default App;
