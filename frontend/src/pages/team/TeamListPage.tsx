import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useTeams, useMyTeams } from '../../hooks/useTeam';
import { useAuth } from '../../hooks/useAuth';
import { TeamCard, TeamCardSkeleton } from '../../components/team/TeamCard';

type TabType = 'public' | 'my';

export function TeamListPage() {
  const [activeTab, setActiveTab] = useState<TabType>('public');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const { isAuthenticated } = useAuth();

  const {
    data: publicTeamsData,
    isLoading: isLoadingPublic,
    error: publicError,
  } = useTeams({ search, page, size: 12 });

  const {
    data: myTeams,
    isLoading: isLoadingMy,
    error: myError,
  } = useMyTeams();

  const teams = activeTab === 'public' ? publicTeamsData?.teams : myTeams;
  const isLoading = activeTab === 'public' ? isLoadingPublic : isLoadingMy;
  const error = activeTab === 'public' ? publicError : myError;
  const total = activeTab === 'public' ? publicTeamsData?.total ?? 0 : myTeams?.length ?? 0;
  const pageSize = 12;
  const totalPages = Math.ceil(total / pageSize);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Teams</h1>
          <p className="mt-1 text-gray-600">
            Browse cycling teams or create your own
          </p>
        </div>
        {isAuthenticated && (
          <Link
            to="/teams/new"
            className="mt-4 sm:mt-0 inline-flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <svg
              className="w-5 h-5 mr-2 -ml-1"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 4v16m8-8H4"
              />
            </svg>
            Create Team
          </Link>
        )}
      </div>

      {isAuthenticated && (
        <div className="border-b border-gray-200 mb-6">
          <nav className="-mb-px flex space-x-8">
            <button
              onClick={() => {
                setActiveTab('public');
                setPage(0);
              }}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'public'
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Public Teams
            </button>
            <button
              onClick={() => {
                setActiveTab('my');
                setPage(0);
              }}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'my'
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              My Teams
            </button>
          </nav>
        </div>
      )}

      {activeTab === 'public' && (
        <div className="mb-6">
          <div className="relative">
            <input
              type="text"
              placeholder="Search teams..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              className="w-full sm:max-w-md px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            />
            <svg
              className="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
        </div>
      )}

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">
            {error instanceof Error ? error.message : 'Failed to load teams'}
          </p>
        </div>
      )}

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <TeamCardSkeleton count={6} />
        </div>
      ) : teams && teams.length > 0 ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {teams.map((team) => (
              <TeamCard
                key={team.id}
                team={team}
                showRole={activeTab === 'my'}
              />
            ))}
          </div>

          {activeTab === 'public' && totalPages > 1 && (
            <div className="mt-8 flex items-center justify-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Previous
              </button>
              <span className="text-sm text-gray-700">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Next
              </button>
            </div>
          )}
        </>
      ) : (
        <div className="text-center py-12">
          <svg
            className="mx-auto h-12 w-12 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
            />
          </svg>
          <h3 className="mt-2 text-sm font-medium text-gray-900">No teams found</h3>
          <p className="mt-1 text-sm text-gray-500">
            {activeTab === 'my'
              ? "You haven't joined any teams yet."
              : 'No public teams match your search.'}
          </p>
          {isAuthenticated && (
            <div className="mt-6">
              <Link
                to="/teams/new"
                className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
              >
                <svg
                  className="w-5 h-5 mr-2 -ml-1"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 4v16m8-8H4"
                  />
                </svg>
                Create a Team
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
