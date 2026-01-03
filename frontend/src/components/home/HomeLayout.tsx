import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '@/config/paths'

export type HomeTab = 'feed' | 'teams'

interface HomeLayoutProps {
  currentTab: HomeTab
  children: React.ReactNode
}

export function HomeLayout({ currentTab, children }: HomeLayoutProps) {
  const { t } = useTranslation('auth')
  const { t: tCommon } = useTranslation('common')

  const tabs: { id: HomeTab; path: string; label: string }[] = [
    {
      id: 'feed',
      path: paths.home(),
      label: t('home.tabs.feed'),
    },
    {
      id: 'teams',
      path: paths.teams(),
      label: tCommon('nav.teams'),
    },
  ]

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">{t('home.title')}</h1>
        <p className="mt-1 text-gray-600">{t('home.subtitle')}</p>
      </div>

      {/* Home Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {tabs.map((tab) => (
            <Link
              key={tab.id}
              to={tab.path}
              className={
                currentTab === tab.id
                  ? 'border-indigo-500 text-indigo-600 py-4 px-1 border-b-2 font-medium text-sm'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 py-4 px-1 border-b-2 font-medium text-sm'
              }
            >
              {tab.label}
            </Link>
          ))}
        </nav>
      </div>

      {/* Page Content */}
      <div>{children}</div>
    </div>
  )
}
