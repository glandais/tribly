import { useState, useEffect } from 'react'
import { Outlet, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Bars3Icon, XMarkIcon } from '@heroicons/react/24/outline'
import { useAuth } from '../../hooks/useAuth'
import { useBreadcrumb } from '../../hooks/useBreadcrumb'
import { Breadcrumb } from './Breadcrumb'
import { Toaster } from '../ui/sonner'
import { LanguageSwitcher } from './LanguageSwitcher'
import { paths } from '@/config/paths'

export function Layout() {
  const { t } = useTranslation('common')
  const { user, isAuthenticated, logout } = useAuth()
  const { items: breadcrumbItems, showBackLink } = useBreadcrumb()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  // Close mobile menu on Escape key
  useEffect(() => {
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && mobileMenuOpen) {
        setMobileMenuOpen(false)
      }
    }

    document.addEventListener('keydown', handleEscape)
    return () => document.removeEventListener('keydown', handleEscape)
  }, [mobileMenuOpen])

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Skip to main content link for keyboard navigation */}
      <a
        href="#main-content"
        className="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-indigo-600 focus:text-white focus:rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
      >
        {t('nav.skipToContent')}
      </a>
      <header className="bg-white shadow-sm">
        <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <Link to="/" className="shrink-0 flex items-center">
                <span className="text-2xl font-bold text-indigo-600">{t('appName')}</span>
              </Link>
            </div>

            {/* Desktop user menu */}
            <div className="hidden sm:flex sm:items-center sm:space-x-4">
              <LanguageSwitcher />
              {isAuthenticated ? (
                <div className="flex items-center space-x-4">
                  <Link
                    to={paths.profile()}
                    className="flex items-center text-sm font-medium text-gray-700 hover:text-gray-900 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:ring-offset-2 rounded px-2 py-1"
                    aria-label={t('nav.profile')}
                  >
                    {user?.avatarUrl ? (
                      <img
                        src={user.avatarUrl}
                        alt={t('nav.profilePicture', { name: user.displayName })}
                        className="h-8 w-8 rounded-full mr-2"
                      />
                    ) : (
                      <div
                        className="h-8 w-8 rounded-full bg-indigo-500 flex items-center justify-center mr-2"
                        aria-label={t('nav.avatar', { name: user?.displayName })}
                      >
                        <span className="text-white text-sm font-medium">
                          {user?.displayName?.charAt(0).toUpperCase()}
                        </span>
                      </div>
                    )}
                    <span className="hidden md:inline">{user?.displayName}</span>
                  </Link>
                  <button
                    onClick={logout}
                    className="text-gray-700 hover:text-gray-900 text-sm font-medium focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:ring-offset-2 rounded px-3 py-1"
                  >
                    {t('nav.signOut')}
                  </button>
                </div>
              ) : (
                <Link
                  to="/login"
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:ring-offset-2"
                >
                  {t('nav.signIn')}
                </Link>
              )}
            </div>

            {/* Mobile menu button */}
            <div className="flex items-center sm:hidden">
              <button
                type="button"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                className="inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-gray-900 hover:bg-gray-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-indigo-500"
                aria-expanded={mobileMenuOpen}
                aria-label={mobileMenuOpen ? t('nav.closeMenu') : t('nav.openMenu')}
              >
                {mobileMenuOpen ? (
                  <XMarkIcon className="h-6 w-6" aria-hidden="true" />
                ) : (
                  <Bars3Icon className="h-6 w-6" aria-hidden="true" />
                )}
              </button>
            </div>
          </div>

          {/* Mobile menu */}
          {mobileMenuOpen && (
            <div className="sm:hidden border-t border-gray-200 pt-4 pb-3">
              <div className="px-3 pb-3">
                <LanguageSwitcher />
              </div>

              <div className="mt-3 pt-3 border-t border-gray-200">
                {isAuthenticated ? (
                  <>
                    <Link
                      to={paths.profile()}
                      className="flex items-center px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-indigo-500"
                      onClick={() => setMobileMenuOpen(false)}
                    >
                      {user?.avatarUrl ? (
                        <img
                          src={user.avatarUrl}
                          alt={t('nav.profilePicture', { name: user.displayName })}
                          className="h-8 w-8 rounded-full mr-3"
                        />
                      ) : (
                        <div
                          className="h-8 w-8 rounded-full bg-indigo-500 flex items-center justify-center mr-3"
                          aria-label={t('nav.avatar', { name: user?.displayName })}
                        >
                          <span className="text-white text-sm font-medium">
                            {user?.displayName?.charAt(0).toUpperCase()}
                          </span>
                        </div>
                      )}
                      <span>{user?.displayName}</span>
                    </Link>
                    <button
                      onClick={() => {
                        logout()
                        setMobileMenuOpen(false)
                      }}
                      className="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-indigo-500"
                    >
                      {t('nav.signOut')}
                    </button>
                  </>
                ) : (
                  <Link
                    to="/login"
                    className="block px-3 py-2 rounded-md text-base font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-indigo-500"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    {t('nav.signIn')}
                  </Link>
                )}
              </div>
            </div>
          )}
        </nav>
      </header>

      <main id="main-content" className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <Breadcrumb items={breadcrumbItems} showBackLink={showBackLink} />
        <Outlet />
      </main>

      <footer className="bg-white border-t border-gray-200 mt-auto">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <p className="text-center text-gray-500 text-sm">
            {t('footer.copyright', { year: new Date().getFullYear() })}
          </p>
        </div>
      </footer>

      <Toaster />
    </div>
  )
}
